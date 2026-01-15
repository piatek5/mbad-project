package com.example.mbad.project.bootstrap;

import com.example.mbad.project.model.*;
import com.example.mbad.project.repository.*;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final RentalRepository rentalRepository;
    private final ReservationRepository reservationRepository;
    private final QueueEntryRepository queueEntryRepository;

    private final PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker();

    @Override
    public void run(String... args) {
        // Zabezpieczenie przed dublowaniem danych przy restarcie
        if (userRepository.count() > 0) {
            System.out.println("Dane już istnieją. Pomijam seedowanie.");
            return;
        }

        System.out.println("--- Rozpoczynam generowanie danych ---");

        // 1. Encje niezależne
        List<Publisher> publishers = initPublishers(10);
        List<Author> authors = initAuthors(20);
        List<Category> categories = initCategories(8);
        List<User> users = initUsers(15);

        // 2. Encje zależne (Książki)
        List<Book> books = initBooks(30, publishers, authors, categories);

        // 3. Encje zależne od Książek (Egzemplarze)
        List<BookCopy> copies = initBookCopies(books); // Tworzy po kilka kopii dla każdej książki

        // 4. Encje 'Obiegowe' (Circulation) - Wypożyczenia, Rezerwacje, Kolejki
        initRentals(20, copies, users);
        initReservations(10, copies, users);
        initQueueEntries(10, books, users);

        System.out.println("--- Zakończono generowanie danych ---");
    }

    // --- 1. WYDAWCY ---
    private List<Publisher> initPublishers(int count) {
        List<Publisher> publishers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            publishers.add(Publisher.builder()
                    .name(faker.book().publisher())
                    .address(faker.address().fullAddress())
                    .build());
        }
        return publisherRepository.saveAll(publishers);
    }

    // --- 2. AUTORZY ---
    private List<Author> initAuthors(int count) {
        List<Author> authors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            authors.add(Author.builder()
                    .firstName(faker.name().firstName())
                    .lastName(faker.name().lastName())
                    // W modelu Author birthYear to String
                    .birthYear(String.valueOf(faker.number().numberBetween(1900, 2000)))
                    .description(faker.lorem().sentence())
                    .build());
        }
        return authorRepository.saveAll(authors);
    }

    // --- 3. KATEGORIE ---
    private List<Category> initCategories(int count) {
        // Używamy Set, żeby nazwy kategorii się nie powtarzały (Unique constraint)
        Set<String> uniqueNames = new HashSet<>();
        while (uniqueNames.size() < count) {
            uniqueNames.add(faker.book().genre());
        }

        List<Category> categories = uniqueNames.stream()
                .map(name -> Category.builder().name(name).build())
                .collect(Collectors.toList());

        return categoryRepository.saveAll(categories);
    }

    // --- 4. UŻYTKOWNICY ---
    private List<User> initUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String username = (firstName + "." + lastName).toLowerCase() + faker.number().randomDigit();
            String phone = faker.phoneNumber().phoneNumber();

            users.add(User.builder()
                    .username(username)
                    .email(faker.internet().emailAddress(username))
                    .phone(phone)
                    .password(passwordEncoder.encode(phone))
                    .build());
        }
        return userRepository.saveAll(users);
    }

    // --- 5. KSIĄŻKI ---
    private List<Book> initBooks(int count, List<Publisher> publishers, List<Author> authors, List<Category> categories) {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            books.add(Book.builder()
                    .isbn(faker.code().isbn13())
                    .title(faker.book().title())
                    .publicationYear(faker.number().numberBetween(1950, 2024))
                    .description(faker.lorem().sentence(3))
                    .publisher(getRandomItem(publishers))
                    // Helper do losowania podzbioru (np. 1-2 autorów, 1-3 kategorie)
                    .authors(getRandomSet(authors, 2))
                    .categories(getRandomSet(categories, 2))
                    .build());
        }
        return bookRepository.saveAll(books);
    }

    // --- 6. EGZEMPLARZE (BookCopy) ---
    private List<BookCopy> initBookCopies(List<Book> books) {
        List<BookCopy> copies = new ArrayList<>();

        // Dla każdej książki stwórz od 1 do 4 egzemplarzy
        for (Book book : books) {
            int copiesCount = faker.number().numberBetween(1, 4);
            for (int i = 0; i < copiesCount; i++) {
                copies.add(BookCopy.builder()
                        .barcode(faker.number().randomNumber(12, true))
                        .usable(true)
                        .available(true)
                        .book(book)
                        .build());
            }
        }
        return bookCopyRepository.saveAll(copies);
    }

    // --- 7. WYPOŻYCZENIA (Rental -> CirculationEntry) ---
    private void initRentals(int count, List<BookCopy> copies, List<User> users) {
        List<Rental> rentals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Instant creationDate = faker.date().past(30, TimeUnit.DAYS).toInstant();

            rentals.add(Rental.builder() // Używamy SuperBuildera
                    .user(getRandomItem(users))
                    .bookCopy(getRandomItem(copies))
                    .creationDate(creationDate)
                    // EndDate może być null (wciąż wypożyczone) lub ustawione (zwrócone)
                    .endDate(faker.bool().bool() ? creationDate.plus(7, ChronoUnit.DAYS) : null)
                    .renewalCount(faker.number().numberBetween(0, 2))
                    .build());
        }
        rentalRepository.saveAll(rentals);
    }

    // --- 8. REZERWACJE (Reservation -> CirculationEntry) ---
    private void initReservations(int count, List<BookCopy> copies, List<User> users) {
        List<Reservation> reservations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Instant creationDate = faker.date().future(5, TimeUnit.DAYS).toInstant();

            reservations.add(Reservation.builder()
                    .user(getRandomItem(users))
                    .bookCopy(getRandomItem(copies))
                    .creationDate(creationDate)
                    .expirationDate(creationDate.plus(3, ChronoUnit.DAYS))
                    .endDate(null) // Rezerwacja aktywna
                    .build());
        }
        reservationRepository.saveAll(reservations);
    }

    // --- 9. KOLEJKI (QueueEntry -> CirculationEntry) ---
    // Zauważ: QueueEntry łączy się z Book, a nie BookCopy!
    private void initQueueEntries(int count, List<Book> books, List<User> users) {
        List<QueueEntry> queueEntries = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            queueEntries.add(QueueEntry.builder()
                    .user(getRandomItem(users))
                    .book(getRandomItem(books)) // Tu referencja do Book
                    .creationDate(Instant.now())
                    .build());
        }
        queueEntryRepository.saveAll(queueEntries);
    }

    // --- HELPERY DO LOSOWANIA ---

    private <T> T getRandomItem(List<T> list) {
        if (list == null || list.isEmpty()) return null;
        return list.get(faker.random().nextInt(list.size()));
    }

    // Zwraca losowy zestaw elementów z listy (np. 2 autorów z listy 20)
    private <T> Set<T> getRandomSet(List<T> list, int maxItems) {
        if (list == null || list.isEmpty()) return Collections.emptySet();
        Set<T> items = new HashSet<>();
        int count = faker.number().numberBetween(1, maxItems + 1);
        while (items.size() < count) {
            items.add(list.get(faker.random().nextInt(list.size())));
        }
        return items;
    }
}
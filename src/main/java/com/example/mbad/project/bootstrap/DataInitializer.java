package com.example.mbad.project.bootstrap;

import com.example.mbad.project.model.*;
import com.example.mbad.project.repository.*;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final int RENEVAL_PERIOD = 30;

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
        List<User> users = initUsers(30);

        // 2. Encje zależne (Książki)
        List<Book> books = initBooks(30, publishers, authors, categories);

        // 3. Encje zależne od Książek (Egzemplarze)
        List<BookCopy> copies = initBookCopies(books); // Tworzy po kilka kopii dla każdej książki

        // 4. Encje 'Obiegowe' (Circulation) - Wypożyczenia, Rezerwacje, Kolejki
        initRentals(30, copies, users);
        initReservations(12, copies, users);
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
                    .birthYear(faker.number().numberBetween(1800, 2000))
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
            String phone = faker.number().digits(9);

            users.add(User.builder()
                    .username(username)
                    .email(faker.internet().emailAddress(username))
                    .phone(phone)
                    .password(passwordEncoder.encode(phone))
                    .role(Role.USER)
                    .build());
        }

        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123")); // Hasło: admin123
            admin.setEmail("admin@biblioteka.pl");
            admin.setRole(Role.ADMIN); // Rola dla adminka

            users.add(admin);
            System.out.println("--- STWORZONO KONTO ADMINA (Login: admin / Pass: admin123) ---");
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
            int copiesCount = faker.number().numberBetween(1, 5);
            for (int i = 0; i < copiesCount; i++) {
                copies.add(BookCopy.builder()
                        .barcode(faker.number().digits(15))
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
            int daysAgo = faker.number().numberBetween(1, 100);
            LocalDate creationDate = LocalDate.now().minusDays(daysAgo);
            LocalDate endDate = faker.bool().bool() ? creationDate.plusDays(faker.number().numberBetween(1, daysAgo + 1)) : null;
            long daysHeld;
            if (endDate != null) {
                daysHeld = ChronoUnit.DAYS.between(creationDate, endDate);
            }
            else {
                daysHeld = ChronoUnit.DAYS.between(creationDate, LocalDate.now());
            }

            int renewalCount = (int) (daysHeld / RENEVAL_PERIOD);
            LocalDate dueDate = creationDate.plusDays(RENEVAL_PERIOD).plusDays((long) RENEVAL_PERIOD * renewalCount);

            rentals.add(Rental.builder() // Używamy SuperBuildera
                    .user(getRandomItem(users))
                    .bookCopy(getRandomItem(copies))
                    .creationDate(creationDate)
                    .endDate(endDate)
                    .renewalCount(renewalCount)
                    .dueDate(dueDate)
                    .build());
        }
        rentalRepository.saveAll(rentals);
    }

    // --- 8. REZERWACJE (Reservation -> CirculationEntry) ---
    private void initReservations(int count, List<BookCopy> copies, List<User> users) {
        List<Reservation> reservations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            LocalDate creationDate = LocalDate.now().minusDays(faker.number().numberBetween(-1, 2));

            reservations.add(Reservation.builder()
                    .user(getRandomItem(users))
                    .bookCopy(getRandomItem(copies))
                    .creationDate(creationDate)
                    .dueDate(creationDate.plusDays(faker.number().numberBetween(1, 4)))
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
                    .creationDate(LocalDate.now())
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
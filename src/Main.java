import java.util.concurrent.Semaphore;
import java.time.LocalTime;

class TicketBookingSystem {
    private int availableTickets;
    private Semaphore semaphore;

    // Конструктор системи бронювання
    public TicketBookingSystem(int ticketCount) {
        this.availableTickets = ticketCount;
        this.semaphore = new Semaphore(1); // Семафор для доступу до ресурсу квитків
    }

    // Метод для бронювання квитка
    public void bookTicket(String customerName) {
        LocalTime currentTime = LocalTime.now();
        // Перевірка часу бронювання
        if (currentTime.isAfter(LocalTime.of(0, 0)) && currentTime.isBefore(LocalTime.of(6, 0))) {
            System.out.println(customerName + " не може забронювати квитки з 00:00 до 06:00. Зачекайте.");
            return;
        }

        try {
            // Клієнт отримує доступ до бронювання
            semaphore.acquire();

            if (availableTickets > 0) {
                System.out.println(customerName + " забронював квиток успішно.");
                availableTickets--;
            } else {
                System.out.println(customerName + ": Всі квитки заброньовані.");
            }
        } catch (InterruptedException e) {
            System.out.println(customerName + ": Виникла помилка під час бронювання.");
        } finally {
            // Звільнення семафору
            semaphore.release();
        }
    }
}

class Customer implements Runnable {
    private String name;
    private TicketBookingSystem system;

    public Customer(String name, TicketBookingSystem system) {
        this.name = name;
        this.system = system;
    }

    @Override
    public void run() {
        system.bookTicket(name);
    }
}

public class Main {
    public static void main(String[] args) {
        // Створення системи з 20 квитками
        TicketBookingSystem system = new TicketBookingSystem(20);

        // Створення 25 клієнтів (потоків)
        for (int i = 1; i <= 25; i++) {
            Thread customer = new Thread(new Customer("Клієнт " + i, system));
            customer.start();
        }
    }
}

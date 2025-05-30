import java.io.*;
import java.util.*;

class Room {
    int roomNumber;
    String category;
    boolean isAvailable;
    double price;

    Room(int roomNumber, String category, double price) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.price = price;
        this.isAvailable = true;
    }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + category + ") - " + (isAvailable ? "Available" : "Booked") + " - ₹" + price;
    }
}

class Reservation {
    String customerName;
    Room room;
    String checkInDate;
    String checkOutDate;

    Reservation(String customerName, Room room, String checkInDate, String checkOutDate) {
        this.customerName = customerName;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    @Override
    public String toString() {
        return "Customer: " + customerName +
               "\nRoom: " + room.roomNumber + " (" + room.category + ")" +
               "\nCheck-in: " + checkInDate +
               "\nCheck-out: " + checkOutDate + "\n";
    }
}

public class HotelReservationSystem {
    static List<Room> rooms = new ArrayList<>();
    static List<Reservation> reservations = new ArrayList<>();
    static final String FILE_NAME = "bookings.txt";

    public static void main(String[] args) {
        initRooms();
        loadBookings();

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Hotel Reservation System ===");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Book a Room");
            System.out.println("3. Cancel Reservation");
            System.out.println("4. View All Bookings");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1: viewAvailableRooms(); break;
                case 2: bookRoom(sc); break;
                case 3: cancelReservation(sc); break;
                case 4: viewBookings(); break;
                case 5: saveBookings(); System.out.println("Thank you!"); return;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }

    static void initRooms() {
        rooms.add(new Room(101, "Standard", 2000));
        rooms.add(new Room(102, "Standard", 2000));
        rooms.add(new Room(201, "Deluxe", 4000));
        rooms.add(new Room(202, "Deluxe", 4000));
        rooms.add(new Room(301, "Suite", 7000));
    }

    static void viewAvailableRooms() {
        System.out.println("\nAvailable Rooms:");
        for (Room room : rooms) {
            if (room.isAvailable) {
                System.out.println(room);
            }
        }
    }

    static void bookRoom(Scanner sc) {
        System.out.print("\nEnter your name: ");
        String name = sc.nextLine();
        System.out.print("Enter room category (Standard/Deluxe/Suite): ");
        String category = sc.nextLine();

        Room selectedRoom = null;
        for (Room room : rooms) {
            if (room.category.equalsIgnoreCase(category) && room.isAvailable) {
                selectedRoom = room;
                break;
            }
        }

        if (selectedRoom == null) {
            System.out.println("No available rooms in this category.");
            return;
        }

        System.out.print("Enter check-in date (dd-mm-yyyy): ");
        String checkIn = sc.nextLine();
        System.out.print("Enter check-out date (dd-mm-yyyy): ");
        String checkOut = sc.nextLine();

        Reservation reservation = new Reservation(name, selectedRoom, checkIn, checkOut);
        reservations.add(reservation);
        selectedRoom.isAvailable = false;

        System.out.println("\nBooking successful! Payment simulation: ₹" + selectedRoom.price + " paid.");
    }

    static void cancelReservation(Scanner sc) {
        System.out.print("\nEnter your name to cancel booking: ");
        String name = sc.nextLine();
        Reservation toCancel = null;

        for (Reservation r : reservations) {
            if (r.customerName.equalsIgnoreCase(name)) {
                toCancel = r;
                break;
            }
        }

        if (toCancel != null) {
            toCancel.room.isAvailable = true;
            reservations.remove(toCancel);
            System.out.println("Reservation cancelled successfully.");
        } else {
            System.out.println("No reservation found under this name.");
        }
    }

    static void viewBookings() {
        System.out.println("\n=== All Bookings ===");
        for (Reservation r : reservations) {
            System.out.println(r);
        }
    }

    static void saveBookings() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Reservation r : reservations) {
                bw.write(r.customerName + "," + r.room.roomNumber + "," + r.room.category + "," +
                        r.checkInDate + "," + r.checkOutDate);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving bookings: " + e.getMessage());
        }
    }

    static void loadBookings() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String name = data[0];
                int roomNumber = Integer.parseInt(data[1]);
                String category = data[2];
                String checkIn = data[3];
                String checkOut = data[4];

                Room bookedRoom = null;
                for (Room room : rooms) {
                    if (room.roomNumber == roomNumber) {
                        bookedRoom = room;
                        room.isAvailable = false;
                        break;
                    }
                }
                if (bookedRoom != null) {
                    reservations.add(new Reservation(name, bookedRoom, checkIn, checkOut));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading bookings: " + e.getMessage());
        }
    }
}

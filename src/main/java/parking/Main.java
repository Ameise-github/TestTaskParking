package parking;

import org.apache.commons.lang3.RandomStringUtils;
import parking.model.Car;
import parking.model.Parking;
import parking.model.Ticket;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    /*Цвета для вывода текста*/
    //region
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    //endregion
    private static Parking parking;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ExecutorService serviceEnter = Executors.newFixedThreadPool(2);
        ExecutorService serviceExit = Executors.newFixedThreadPool(2);
        long countSec;

        while (true) {
            System.out.print("Введите количество мест на парковке: ");
            String s = sc.nextLine();
            try {
                int countPlece = Integer.parseInt(s);
                System.out.print("Введите время заезда (в сек): ");
                countSec = Long.parseLong(sc.nextLine());
                if (countSec > 5 || countSec < 1) {
                    System.out.println(ANSI_RED + "Разрешенное время заезда от 1 до 5 сек!" + ANSI_RESET);
                    continue;
                }
                parking = new Parking(countPlece, countSec);
                break;
            } catch (Exception e) {
                System.out.println("Введите число!");
            }
        }

        while (true) {
            System.out.println("\nВведите команду (или help): ");
            System.out.print(">> ");
            String command = sc.nextLine().toLowerCase();
            String valueTicket = "";

            String[] temp = command.split(":");
            if (temp.length > 1) {
                valueTicket = temp[1];
            }

            switch (temp[0]) {
                case "l":
                    if (parking.getCars().size() == 0) {
                        System.out.println("Парковка пустая.");
                    } else {
                        System.out.println("Список машин на парковке: ");
                        parking.getCarsPrint();
                    }
                    continue;
                case "c":
                    System.out.println("Количество оставшихся мест на парковке: " + parking.countRemainingPlace());
                    continue;
                case "help":
                    help();
                    continue;
                case "p":
                    try {
                        int valTicketTmp = Integer.parseInt(valueTicket);
                        if (parking.countRemainingPlace() != 0 && parking.countRemainingPlace() != parking.getCountPlace()) {
                            System.out.println(ANSI_RED + "Машины стоят в очереди!" + ANSI_RESET);
                        }
                        commandPN(valTicketTmp, serviceEnter, countSec);
                    } catch (NumberFormatException e) {
                        System.out.println(ANSI_RED + "Некорректный ввод количества въезжающих машин!" + ANSI_RESET);
                    } finally {
                        continue;
                    }
                case "u":
                    try {
                        commandUN(valueTicket, serviceExit);
                    } catch (Exception ex) {
                        System.out.println(ANSI_RED + "Некорректный ввод номера билета машины!" + ANSI_RESET);
                    } finally {
                        continue;
                    }
                case "e":
                    serviceEnter.shutdown();
                    serviceExit.shutdown();
                    try {
                        if (!serviceEnter.awaitTermination(20, TimeUnit.SECONDS) || !serviceExit.awaitTermination(20, TimeUnit.SECONDS)){
                            serviceEnter.shutdownNow();
                            serviceExit.shutdownNow();
                            if (!serviceEnter.awaitTermination(20, TimeUnit.SECONDS) || !serviceExit.awaitTermination(20, TimeUnit.SECONDS))
                                System.err.println("Pool did not terminate");
                        }
                    }catch (InterruptedException e){
                        serviceEnter.shutdownNow();
                        serviceExit.shutdownNow();
                    }
                    System.out.println(ANSI_RED + "Программа завершила работу!" + ANSI_RESET);
                    System.exit(0);
                    break;
                default:
                    System.out.println(ANSI_RED + "Введенная команда не распознана. Попробуйте еще раз!\n" + ANSI_RESET);
                    help();
            }
        }
    }

    /**
     * формирование номеров машин
     */
    public static Car getNewCar() {
        String serial = RandomStringUtils.randomAlphabetic(3).toUpperCase();
        String numb = RandomStringUtils.randomNumeric(3);
        String region = RandomStringUtils.randomNumeric(2).toUpperCase();
        String numberCar = serial.charAt(0) + numb + serial.substring(1) + region;
        return new Car(numberCar);
    }

    /**
     * help
     */
    private static void help() {
        System.out.println("Введите одну из следующих команд: ");
        System.out.println(ANSI_RED + "p:N" + ANSI_RESET + " - ПРИПАРКОВАТЬ N МАШИН.");
        System.out.println(ANSI_RED + "u:N" + ANSI_RESET + " - ВЫЕХАТЬ С N-ОГО МЕТА ПАРКОВКИ.");
        System.out.println(ANSI_RED + "u:[1,2,...n]" + ANSI_RESET + " - ВЫЕХАТЬ С НЕСКОЛЬКИХ ПАРКОВОЧНЫХ МЕСТ.");
        System.out.println(ANSI_RED + "l" + ANSI_RESET + " - СПИСОК МАШИН.");
        System.out.println(ANSI_RED + "c" + ANSI_RESET + " - УЗНАТЬ КОЛИЧЕСТВО ОСТАВШИХСЯ МЕСТ НА ПАРКОВКЕ.");
        System.out.println(ANSI_RED + "e" + ANSI_RESET + "- ВЫХОД ИЗ ПРИЛОЖЕНИЯ");
    }

    //обработка команды P:N
    private static void commandPN(int valueTicket, ExecutorService serviceEnter, long countSec) {
        for (int i = 0; i < valueTicket; i++) {
            if (parking.countRemainingPlace() != 0) {
                serviceEnter.submit(() -> {
                    parking.parkingEntrance(getNewCar());
                });
            } else {
                System.out.println(ANSI_RED + "Извините,парковка заполнена." + ANSI_RESET);
                break;
            }
        }

    }

    //обработка команды U:N или U:[2,54, .. n]
    private static void commandUN(String valueTicket, ExecutorService serviceExit) {
        //если перечисление, то выезжают несколько машин, иначе - одна
        if (valueTicket.contains(",")) {
            String[] v = (valueTicket.substring(1, valueTicket.length() - 1)).split(",");
            for (String s : v) {
                int tmpNumberTicket = Integer.parseInt(s);
                //проверить номер билета
                Ticket ticketFind = parking.checkedTicket(tmpNumberTicket);
                if (ticketFind != null) {
                    //создать поток
                    serviceExit.submit(() -> {
                        parking.parkingExit(ticketFind);
                    });
                } else {
                    System.out.println("\nНет машин с номером билета: " + tmpNumberTicket);
                }
            }
        } else {
            int vt = Integer.parseInt(valueTicket);
            //проверить номер билета
            Ticket ticketFind = parking.checkedTicket(vt);
            if (ticketFind != null) {
                serviceExit.submit(() -> parking.parkingExit(ticketFind));
            } else {
                System.out.println("\nНет машин с номером билета: " + vt);
            }
        }
    }
}

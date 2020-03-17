package parking;

import org.apache.commons.lang3.RandomStringUtils;
import parking.model.Car;
import parking.model.Parking;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {
    /*Цвета для вывода текста*/
    //region
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    //endregion
    private static Parking parking;
    private static long countSec = 0;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
//        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
//        List<Car> cars = new ArrayList<>();

        Timer timer = new Timer();

        while (true) {
            System.out.print("Введите количество мест на парковке: ");
            String s = sc.nextLine();
            try {
                int countPlece = Integer.parseInt(s);
                parking = new Parking(countPlece);
                System.out.print("Введите время заезда (в сек): ");
                countSec = Long.parseLong(sc.nextLine());
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
                        int tmp = Integer.parseInt(valueTicket);
//                        service.schedule(() -> parking.parkingEntrance(getNewCar()), countSec, TimeUnit.SECONDS);
                            //Планирование выполнения задачи
                            timer.schedule(
                                    // //Задача на выполнение
                                    new TimerTask() {
                                        @Override
                                        public void run() {
                                            try {
                                                for (int i = 0; i < tmp; i++){
                                                    TimeUnit.SECONDS.sleep(countSec);
                                                    parking.parkingEntrance(getNewCar());
                                                }

                                            } catch (InterruptedException e) {
                                                System.err.println("Задача прервана. error= ");
                                                e.printStackTrace();
                                            }
                                        }
                                    }, 0); //сначала задержка на countSec, а потом выполнение задачи
                    } catch (NumberFormatException e) {
                        System.out.println("Некорректный ввод количества въехжающих машин!");
                    } finally {
                        continue;
                    }
                case "u":
                    try {
                        String[] v = (valueTicket.substring(1, valueTicket.length() - 1)).split(",");
                        //если перечисление, то выезжают несколько машин, иначе - одна
                        if (v.length > 1) {
                            for (String s : v) {
                                int tmpNumberTicket = Integer.parseInt(s);
                                Thread thread = new Thread(() -> parking.parkingExit(tmpNumberTicket));
                                thread.start();
//                                timer.schedule(
//                                        // //Задача на выполнение
//                                        new TimerTask() {
//                                            @Override
//                                            public void run() {
//                                                parking.parkingExit(tmpNumberTicket);
//                                            }
//                                        }, 0);
                            }
                        } else {
                            int vt = Integer.parseInt(valueTicket);
                            Thread thread = new Thread(() -> parking.parkingExit(vt));
                            thread.start();
                        }
                    } catch (Exception ex) {
                        System.out.println("Некорректный ввод номера билета машины!");
                    } finally {
                        continue;
                    }
                case "e":
                    System.out.println("Программа завершила работу!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Введенная команда не распознана. Попробуйте еще раз!\n");
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

    /*Преобразование в int*/
    private static Integer tryParsInt(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}

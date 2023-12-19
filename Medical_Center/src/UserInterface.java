import java.util.Scanner;

public class UserInterface {
    public static void main(String[] args) {
        try {
            MedicalServices services = new MedicalServices();
            while (true) {
                System.out.println("Please select type of user: \n 1-Admin\n 2-Doctor\n 3-Patient\n 4-Exit");
                int input = Integer.parseInt(getUserInput(null));

                // ADMIN services
                if (input == 1) {
                    System.out.println(
                            "Please select one of the following: \n 1-Add a specialization\n 2-Add a doctor to the system\n 3-Delete a doctor from the system\n 4-See a list of all registered patients\n 5-See a list of all upcoming appointments\n 6-See a list of a patient's medical records\n 7-See a list of all patients and their total sum of visit costs");
                    input = Integer.parseInt(getUserInput(null));

                    if (input == 1) {
                        services.addSpecialization(getUserInput("Specialization?"),
                                Integer.parseInt(getUserInput("Visit cost for specialization?")));
                    } else if (input == 2) {
                        String firstName = getUserInput("First Name: ");
                        String lastName = getUserInput("Last Name: ");
                        services.listSpecializations();
                        //Fetches a number from a list, needs to be converted in addDoctor()
                        int specialization = Integer.parseInt(getUserInput("Select number: "));
                        String phoneNbr = getUserInput("Phone number: ");
                        services.addDoctor(firstName, lastName, specialization, phoneNbr);
                    } else if (input == 3) {
                        System.out.println("Not implemented");
                    } else if (input == 4) {
                        services.printAllPatients();
                    } else if (input == 5) {
                        services.listAllUpcomingAppointments();
                    } else if (input == 6) {
                        services.listAllMedicalRecordsOfPatient(getUserInput("Input medical number of patient:"));
                    }
                    else if (input == 7) {
                        System.out.println("not implemented");
                    }

                    // DOCTOR SERVICES
                } else if (input == 2) {
                    System.out.println(
                            "Please select one of the following: \n 1-Alter availabilities\n 2-See a list of all upcoming appointments\n 3-See a list of all patients\n 4-Add medical record to a patient\n 5-See a list of all upcoming appointments\n 6-See a list of a patient's medical records");
                    input = Integer.parseInt(getUserInput(null));

                    // PATIENT services
                } else if (input == 3) {
                    System.out.println(
                            "Please select one of the following: \n 1-Sign up\n 2-Edit information\n 3-Book an appointment\n 4-View diagnosis and perscription by doctor");
                    input = Integer.parseInt(getUserInput(null));
                } else if (input == 4) {
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getUserInput(String msg) {
        Scanner scan = new Scanner(System.in);
        if (msg != null) {
            System.out.println(msg);
        }
        String input = scan.nextLine();
        return input;
    }
}

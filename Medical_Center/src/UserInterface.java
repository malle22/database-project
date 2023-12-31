import Utilities.InputValidator;

import java.sql.Date;
import java.sql.Time;
import java.util.Scanner;

public class UserInterface {
    public static void main(String[] args) {
        try {
            MedicalServices services = new MedicalServices();
            InputValidator inputvalidator = new InputValidator();
            while (true) {
                System.out.println("Please select type of User: \n 1-Admin\n 2-Doctor\n 3-Patient\n 4-Exit");
                int input = Integer.parseInt(getUserInput(null));

                // ADMIN services
                if (input == 1) {
                    System.out.println(
                            "Please select one of the following: \n 0-Return to Main Menu\n 1-Add a specialization\n 2-Add a doctor to the system\n 3-Delete a doctor from the system\n 4-See a list of all registered patients\n 5-See a list of all upcoming appointments\n 6-See a list of a patient's medical records\n 7-See a list of all patients and their total sum of visit costs\n 8-See a list of all registered doctors");
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
                        services.listDoctors();
                        String doctorID = getUserInput("Delete Doctor (ID): ");
                        while (!inputvalidator.isValidDigitUserInput(doctorID, 8)) {
                            System.out.println("Wrong input, try again.");
                            doctorID = getUserInput("Delete Doctor (ID): ");
                        }
                        services.deleteDoctor(doctorID);
                    } else if (input == 4) {
                        services.listPatients();
                    } else if (input == 5) {
                        services.listAllUpcomingAppointments();
                    } else if (input == 6) {
                        services.listAllMedicalRecordsOfPatient(getUserInput("Input medical number of patient:"));
                    } else if (input == 7) {
                        services.listPatientsAndCost();
                    } else if(input == 8){
                        services.listDoctors();
                    } else if (input == 0){
                        //return to main menu
                    }
                    // DOCTOR SERVICES
                } else if (input == 2) {
                    System.out.println(
                            "Please select one of the following: \n 0-Return to Main Menu \n 1-Alter availability\n 2-See a list of all upcoming appointments\n 3-Add a medical record to a patient");
                    input = Integer.parseInt(getUserInput(null));
                    if(input == 1){
                        String eNum= getUserInput("Input employee number: 8 digits");
                        services.printDoctorsAvailabilities(eNum);
                        System.out.println("Enter changes or press 0 to return.");
                        String inputDay = getUserInput("Input day: format 'Monday'");
                        if(inputDay.equals("0")) continue;
                        services.changeAvailability(eNum, inputDay, Time.valueOf(getUserInput("Input time: format 'HH:mm:ss'")));

                    }
                    else if(input == 2){
                        services.listAllUpcomingAppointmentsOfDoctor(getUserInput("Input employee number: 8 digits"));
                    }
                    else if(input == 3){
                        String eNum = getUserInput("Enter employee number: 8-digit");
                        boolean havePatients = services.listDoctorsPatients(eNum);
                        if (!havePatients){
                            System.out.println("You have no patients.");
                        }
                        String mNum = getUserInput("Enter medical number (or press 0 to return): 8-digit");
                        if (mNum.equals("0") ) continue;
                        services.addMedicalRecord(getUserInput("Enter diagnosis:"), getUserInput("Enter description"), getUserInput("Enter perscription:"), mNum, eNum);

                    }else if (input == 0){
                        //return to main menu
                    }

                    // PATIENT services
                } else if (input == 3) {
                    System.out.println(
                            "Please select one of the following: \n 0-Return to Main Menu\n 1-Sign up\n 2-Edit information\n 3-Book an appointment\n 4-View diagnosis and perscription by doctor");
                    input = Integer.parseInt(getUserInput(null));
                    if(input==1){
                        String firstName = getUserInput("Input first name:");
                        String lastName = getUserInput("Input last name:");
                        services.printGenderList();
                        int genderIndex = Integer.parseInt(getUserInput("Input gender:"));
                        String address = getUserInput("Input address:");
                        String phone = getUserInput("Input phone number: 10-digits");
                        Date birthDate = Date.valueOf(getUserInput("Input date of birth: xxxx-xx-xx"));
                        services.addPatient(firstName, lastName, genderIndex, address, phone, birthDate);
                    }
                    else if(input==2){
                        String mNum = getUserInput("Input medical number:");
                        System.out.println("Only enter the fields you want to edit, leave the others blank");
                        String firstName = getUserInput("New firstname:");
                        String lastName = getUserInput("New lastname:");
                        String address = getUserInput("New address:");
                        String phone = getUserInput("New phone number:");
                        services.updatePatientInformation(mNum,firstName,lastName,address,phone);
                    }
                    else if(input==3){
                        String mNum = getUserInput("Input medical number:");
                        services.listSpecializations();
                        String specializationString = getUserInput("What type of medical service are you in need of?");
                        int specializationIndex = Integer.parseInt(specializationString);
                        services.printDoctorsOfSpecialization(specializationIndex);
                        String eNum = getUserInput("Input employee number:");
                        services.printDoctorsAvailabilities(eNum);
                        String day = getUserInput("Choose day:");
                        Time time = Time.valueOf(getUserInput("Choose time:"));
                        services.bookAppointment(eNum,day,time,mNum);
                    }
                    else if(input==4){
                        String mNum = getUserInput("Enter medical number: ");
                        while(!inputvalidator.isValidDigitUserInput(mNum,8)){
                            if(mNum.equals("0")) break;
                            System.out.printf("Invalid input- try again or press 0 to return %n");
                            mNum = getUserInput(null);
                        }
                        if(inputvalidator.isValidDigitUserInput(mNum, 8)){
                            services.listDiagnosisAndPrescription(mNum);
                        }
                    }
                } else if (input == 4) {
                    System.exit(0);
                } else if (input == 0) {
                    //return to Main Menu
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
        // If the user leaves the field blank, return null
        return input.trim().isEmpty() ? null : input;
    }

}

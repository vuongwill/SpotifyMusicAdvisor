package advisor;
import java.util.*;

public class Main {
    public static boolean auth = false;
    public static void main(String[] args) {
        Auth authorize = new Auth();
        Pages pages = new Pages();

        if (args.length > 0) {
            if (args[0].equals("-access")) {
                authorize.server = args[1];
            }
            if (args[2].equals("-resource")) {
                authorize.apiUrl = args[3];
            }
            if (args[4].equals("-page")) {
                pages.perPage = Integer.parseInt(args[5]);
            }
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (auth == false) {
                System.out.println("Commands:\n" +
                        "(\"next\" and \"prev\" to navigate pages)\n" +
                        "(\"exit\" to return to main menu)\n\n" +
                        "Authenticate (Please type \"auth\" to authenticate)\n" +
                        "New Music (\"new\")\n" +
                        "Featured playlists (\"featured\")\n" +
                        "All categories (\"categories\")\n" +
                        "Playlists (\"playlists + category\")\n\n" +
                        "Leave program (\"stop\")\n");
            }
            if (auth == true) {
                System.out.println("Commands:\n" +
                        "(\"next\" and \"prev\" to navigate pages)\n" +
                        "(\"exit\" to return to main menu)\n\n" +
                        "Authenticated, thank you!\n" +
                        "New Music (\"new\")\n" +
                        "Featured playlists (\"featured\")\n" +
                        "All categories (\"categories\")\n" +
                        "Playlists (\"playlists + category\")\n\n" +
                        "Leave program (\"stop\")\n");
            }

            String input = scanner.nextLine();
            if (input.equals("stop")) {
                System.out.println("Goodbye!");
                break;
            }
            if (input.equals("auth")) {
                authorize.requestCode();
                authorize.requestToken();
                auth = true;
            }
            if (!auth) {
                System.out.println("Please, provide access for application.");
            }
            if (input.contains("new") && auth) {
                pages.print(authorize.listNew());
            }
            if (input.contains("featured") && auth) {
                pages.print(authorize.listFeatured());
            }
            if (input.contains("categories") && auth) {
                pages.print(authorize.listCategories());
            }
            if (input.contains("playlists") && auth) {
                String id = input.substring(10).trim().toLowerCase();
                pages.print(authorize.listPlaylists(id));
            }
//            if (input.contains("all") && auth) {
//                authorize.browseCategories("EQUAL");
//            }
        }
    }
}

package advisor;

import java.util.List;
import java.util.Scanner;

public class Pages {
    public int perPage = 5;
    Scanner scanner = new Scanner(System.in);

    public void print(List<SongInfo> list) {
        int pages = (int) Math.ceil((double) list.size() / perPage);
        int currentPage = 1;
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < perPage; i++) {
            str.append(list.get(i).getName() + "\n");
            if (list.get(i).getArtist() != null) {
                str.append("[" + list.get(i).getArtist() + "]" + "\n");
            }
            if (list.get(i).getLink() != null) {
                str.append(list.get(i).getLink() + "\n\n");
            }
        }
        System.out.println("");
        System.out.println(str);
        System.out.printf("---PAGE %d OF %d---%n%n", currentPage, pages);

        while (true) {
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                break;
            }
            if (input.equals("next")) {
                if (currentPage < pages) {
                    currentPage++;
                } else {
                    System.out.println("No more pages");
                    continue;
                }
            }
            if (input.equals("prev")) {
                if (currentPage > 1) {
                    currentPage--;
                } else {
                    System.out.println("No more pages");
                    continue;
                }
            }
            str.setLength(0);
            for (int i = (currentPage * perPage) - perPage; i < currentPage * perPage; i++) {
                str.append(list.get(i).getName() + "\n");
                if (list.get(i).getArtist() != null) {
                    str.append("[" + list.get(i).getArtist() + "]" + "\n");
                }
                if (list.get(i).getLink() != null) {
                    str.append(list.get(i).getLink() + "\n\n");
                }
            }
            System.out.println("");
            System.out.println(str);
            System.out.printf("---PAGE %d OF %d---%n%n", currentPage, pages);
            continue;
        }
    }
}

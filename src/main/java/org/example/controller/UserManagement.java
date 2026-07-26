package org.example.controller;

import org.example.model.Member;
import org.example.utils.DateUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserManagement {
    private static final String FILE_PATH = "src/main/java/org/example/data/members.txt";

    // Load danh sách thành viên từ file users.txt
    public static List<Member> loadUsers() {
        List<Member> members = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 4) {
                    String id = p[0];
                    String name = p[1];
                    String username = p[2];
                    String password = p[3];

                    Member m = new Member(id, name, username, password);
                    for (int i = 4; i + 2 < p.length; i += 3) {
                        String bookId = p[i];
                        Date start = DateUtils.parse(p[i + 1]);
                        Date end = DateUtils.parse(p[i + 2]);
                        m.borrowBook(new org.example.model.Loan(bookId, start, end));
                    }
                    members.add(m);
                }
            }
        } catch (IOException e) {
            System.out.println("⚠ Lỗi đọc danh sách thành viên: " + e.getMessage());
        }
        return members;
    }

    // Tìm user theo username và password
    public static Member findUser(String username, String password) {
        for (Member m : loadUsers()) {
            if (m.getUsername() != null && m.getUsername().equals(username) && m.getPassword().equals(password)) {
                return m;
            }
        }
        return null;
    }

    // Đổi mật khẩu
    public static boolean changePassword(String username, String oldPass, String newPass) {
        List<Member> members = loadUsers();
        boolean updated = false;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Member m : members) {
                if (m.getUsername().equals(username) && m.getPassword().equals(oldPass)) {
                    m.setPassword(newPass);
                    updated = true;
                }
                bw.write(m.getId() + "," + m.getName() + "," + m.getUsername() + "," + m.getPassword());
                for (org.example.model.Loan l : m.getLoans()) {
                    bw.write("," + l.getBookId() + "," + DateUtils.format(l.getStartDate()) + "," + DateUtils.format(l.getEndDate()));
                }
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("⚠ Lỗi đổi mật khẩu: " + e.getMessage());
        }

        return updated;
    }

    public static boolean usernameExists(String username) {
        for(Member m : loadUsers()) {
            if(m.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
        // return loadUsers().stream().anyMatch(m -> m.getUsername().equals(username)); có thể đổi qua lambda cho clean
    }

    public static void saveUser(Member m) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(m.getId() + "," + m.getName() + "," + m.getUsername() + "," + m.getPassword());
            for (org.example.model.Loan l : m.getLoans()) {
                bw.write("," + l.getBookId() + "," + DateUtils.format(l.getStartDate()) + "," + DateUtils.format(l.getEndDate()));
            }
            bw.newLine();
        } catch (IOException e) {
            System.out.println("⚠ Lỗi lưu thành viên: " + e.getMessage());
        }
    }
}
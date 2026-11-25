import java.io.*;
import java.util.*;

class Book {
    String id, title, author, issuedTo;
    Queue<String> q = new LinkedList<String>();
    Book(String i, String t, String a) { id = i; title = t; author = a; }
    public String toString() { return id + " | " + title + " | " + author; }
}

class Node {
    Book b; Node next;
    Node(Book b) { this.b = b; }
}

public class dsa {
    static Node head = null;
    static Map<String, Book> map = new HashMap<String, Book>();
    static int nextId = 101;
    static final String FILE = "library.txt";

    static boolean empty(String s) { return s == null || s.trim().isEmpty(); }

    static void append(Book b) {
        Node n = new Node(b);
        if (head == null) head = n;
        else { Node c = head; while (c.next != null) c = c.next; c.next = n; }
        map.put(b.id, b);
    }

    static void add(String t, String a) {
        if (empty(t) || empty(a)) { System.out.println("Title/author cannot be empty."); return; }
        Book b = new Book("B" + nextId++, t.trim().replace("|", "/"), a.trim().replace("|", "/"));
        append(b);
        System.out.println("Book added. " + b);
    }

    static void display() {
        if (head == null) { System.out.println("No books."); return; }
        for (Node c = head; c != null; c = c.next)
            System.out.println(c.b + (c.b.issuedTo != null ? " [Issued to " + c.b.issuedTo + "]" : ""));
    }

    static void searchId(String id) {
        id = id.trim().toUpperCase();
        Book b = map.get(id);
        System.out.println(b == null ? "Book " + id + " not found." : "Found: " + b);
    }

    static void searchTitle(String k) {
        boolean found = false;
        for (Node c = head; c != null; c = c.next)
            if (c.b.title.toLowerCase().contains(k.trim().toLowerCase())) {
                System.out.println("Found: " + c.b); found = true;
            }
        if (!found) System.out.println("No book found.");
    }

    static void remove(String id) {
        id = id.trim().toUpperCase();
        if (!map.containsKey(id)) { System.out.println("Book " + id + " not found."); return; }
        map.remove(id);
        if (head.b.id.equals(id)) head = head.next;
        else {
            Node c = head;
            while (!c.next.b.id.equals(id)) c = c.next;
            c.next = c.next.next;
        }
        System.out.println("Book " + id + " removed.");
    }

    static void issue(String id, String s) {
        id = id.trim().toUpperCase();
        Book b = map.get(id);
        if (b == null) { System.out.println("Book " + id + " not found."); return; }
        if (empty(s)) { System.out.println("Student name cannot be empty."); return; }
        s = s.trim().replace(",", " ");
        if (b.issuedTo == null) {
            b.issuedTo = s;
            System.out.println("Book " + id + " issued to " + s + ".");
        } else {
            b.q.add(s);
            System.out.println(s + " added to waiting queue. Position: " + b.q.size());
        }
    }

    static void ret(String id) {
        id = id.trim().toUpperCase();
        Book b = map.get(id);
        if (b == null) { System.out.println("Book " + id + " not found."); return; }
        if (b.issuedTo == null) { System.out.println("Book " + id + " is not issued."); return; }
        if (!b.q.isEmpty()) {
            b.issuedTo = b.q.poll();
            System.out.println("Book " + id + " assigned to " + b.issuedTo + ".");
        } else {
            b.issuedTo = null;
            System.out.println("Book " + id + " returned. Now available.");
        }
    }

    static Node sort(Node h) {
        if (h == null || h.next == null) return h;
        Node slow = h, fast = h.next;
        while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; }
        Node right = slow.next;
        slow.next = null;
        Node x = sort(h), y = sort(right);
        Node dummy = new Node(null), tail = dummy;
        while (x != null && y != null) {
            if (x.b.title.compareToIgnoreCase(y.b.title) <= 0) { tail.next = x; x = x.next; }
            else { tail.next = y; y = y.next; }
            tail = tail.next;
        }
        tail.next = (x != null) ? x : y;
        return dummy.next;
    }

    static void load() {
        File f = new File(FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p[0].equals("NEXT")) { nextId = Integer.parseInt(p[1]); continue; }
                if (p.length < 5) continue;
                Book b = new Book(p[0], p[1], p[2]);
                if (!p[3].equals("-")) b.issuedTo = p[3];
                if (!p[4].isEmpty()) b.q.addAll(Arrays.asList(p[4].split(",")));
                append(b);
            }
        } catch (Exception e) {
            System.out.println("Could not read " + FILE);
        }
    }

    static void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            pw.println("NEXT|" + nextId);
            for (Node c = head; c != null; c = c.next)
                pw.println(c.b.id + "|" + c.b.title + "|" + c.b.author + "|"
                        + (c.b.issuedTo == null ? "-" : c.b.issuedTo) + "|" + String.join(",", c.b.q));
        } catch (IOException e) {
            System.out.println("Save error.");
        }
    }

    public static void main(String[] args) {
        load();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("\n1.Add 2.Display 3.SearchID 4.SearchTitle 5.Issue 6.Return 7.Remove 8.Sort 9.Exit\nChoice: ");
            if (!sc.hasNextLine()) { save(); return; }
            String c = sc.nextLine().trim();
            if (c.equals("1")) {
                System.out.print("Title: ");  String t = sc.nextLine();
                System.out.print("Author: "); String a = sc.nextLine();
                add(t, a);
            } else if (c.equals("2")) {
                display();
            } else if (c.equals("3")) {
                System.out.print("Book ID: "); searchId(sc.nextLine());
            } else if (c.equals("4")) {
                System.out.print("Title: "); searchTitle(sc.nextLine());
            } else if (c.equals("5")) {
                System.out.print("Book ID: "); String id = sc.nextLine();
                System.out.print("Student: "); issue(id, sc.nextLine());
            } else if (c.equals("6")) {
                System.out.print("Book ID: "); ret(sc.nextLine());
            } else if (c.equals("7")) {
                System.out.print("Book ID: "); remove(sc.nextLine());
            } else if (c.equals("8")) {
                head = sort(head); System.out.println("Sorted by title.");
            } else if (c.equals("9")) {
                save(); System.out.println("Saved. Bye!"); return;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }
}
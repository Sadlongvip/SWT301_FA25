package traitb.example;

enum Constants {;
    int maxUsers = 100;
}

class InterfaceFieldModificationExample {
    public static void main(String[] args) {
        // Constants.MAX_USERS = 200; // Compile-time error
    }
}

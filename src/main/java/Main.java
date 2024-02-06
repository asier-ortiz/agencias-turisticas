import util.MethodsUtil;
import view.Login;

public class Main {
    public static void main(String[] args) {
        MethodsUtil.setApplicationLookAndFeel();
        Login login = new Login();
        login.setContentPane(login.getLoginWindow());
        login.pack();
        login.setLocationRelativeTo(null);
        login.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        login.setVisible(true);
        System.out.println("LOGIN PARA TODAS LAS BASES DE DATOS: asier@test.com / 123456");
        System.out.println("LOGIN PARA TODAS LAS BASES DE DATOS: asier@test.com / 123456");
        System.out.println("LOGIN PARA TODAS LAS BASES DE DATOS: asier@test.com / 123456");
        System.out.println("LOGIN PARA TODAS LAS BASES DE DATOS: asier@test.com / 123456");
        System.out.println("LOGIN PARA TODAS LAS BASES DE DATOS: asier@test.com / 123456");
        System.out.println("LOGIN PARA TODAS LAS BASES DE DATOS: asier@test.com / 123456");
        System.out.println("LOGIN PARA TODAS LAS BASES DE DATOS: asier@test.com / 123456");
        System.out.println("LOGIN PARA TODAS LAS BASES DE DATOS: asier@test.com / 123456");
    }
}
package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

class MuseumListProxyTest {
    @Test
    public void loadMuseumsTest() throws SQLException {
        MuseumListInterface mlp = new MuseumListProxy();
        mlp.getMuseums("musei per ciechi", null, false);
        ArrayList<Museum> m1 = mlp.loadMuseums(0, 10);
        Assertions.assertEquals(10, m1.size());
        ArrayList<Museum> m3 = mlp.loadMuseums(0, 10);
        for (int i = 0; i < 10; i++) {
            Assertions.assertEquals(m1.get(i).getName(), m3.get(i).getName());
        }
        ArrayList<Museum> m2 = mlp.loadMuseums(10, 20);
        for (int i = 0; i < 10; i++) {
            Assertions.assertNotEquals(m1.get(i).getName(), m2.get(i).getName());
        }
        m1.addAll(m2);
        Assertions.assertEquals(20, m1.size());
    }

    @Test
    public void getMuseumsTest() throws SQLException {
        MuseumListInterface mlp = new MuseumListProxy();
        Assertions.assertThrows(NullPointerException.class, () -> mlp.loadMuseums(0, 10));
        mlp.getMuseums("museo", null, false);
        Assertions.assertDoesNotThrow(() -> mlp.loadMuseums(0, 10));
    }
}

package com.pixurvival.core.playground;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.yaml.snakeyaml.Yaml;

import java.io.StringWriter;

public class TestYaml {

    public enum E {
        A,
        B,
        C;
    }

    @Data
    @AllArgsConstructor
    public static class B {
        String saucisse;
        double doubli;
        int i = 0;
        String caca = "caca";
        String[] table;
    }

    @Data
    public static class A {
        String s = "coucou";
        int i = 54;
        E e = E.B;
    }

    public static void main(String[] args) {
        Yaml yaml = new Yaml();
        StringWriter w = new StringWriter();
        yaml.dump(new A(), w);
        System.out.println(w);
    }

    // public static void main(String[] args) {
    // Yaml yaml = new Yaml();
    // String data = "!!com.pixurvival.core.playground.TestYaml$A {s: 'test', i:
    // 'test'}";
    // A a = yaml.loadAs(new StringReader(data), A.class);
    // System.out.println(a);
    // }
}

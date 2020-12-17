package com.pixurvival.core.reflection.visitor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.Data;

public class VisitorTest {

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Traverse {
	}

	@Data
	public static class A {
		private String string;
		private int integer;
		private @Traverse B b;
		private @Traverse C c;
		private @Traverse Map<String, C> map = new HashMap<>();

	}

	@Data
	public static class B {
		private int integer;
		private @Traverse C c;
		private @Traverse List<C> list = new ArrayList<>();
	}

	@Data
	public static class C {
		private double dooble;
	}

	public static class LogVisitHandler implements VisitHandler {

		private List<String> logs = new ArrayList<>();

		@Override
		public void visit(VisitNode node) {
			logs.add(node.pathString());
		}

		public Object[] getLogs() {
			return logs.toArray();
		}

	}

	@Test
	public void visitTest() {

		C c = new C();
		c.setDooble(2.5);

		B b = new B();
		b.setInteger(2);
		b.setC(c);
		b.getList().add(new C());
		b.getList().add(new C());
		b.getList().add(new C());

		C c2 = new C();
		c2.setDooble(3.25);

		A a = new A();
		a.setString("coucou");
		a.setInteger(6);
		a.setB(b);
		a.setC(c2);
		a.getMap().put("key1", new C());
		a.getMap().put("key2", new C());

		LogVisitHandler handler = new LogVisitHandler();

		VisitorContext context = new VisitorContext();
		context.setTraversalCondition(n -> !(n.getKey() instanceof Field) || ((Field) n.getKey()).isAnnotationPresent(Traverse.class));
		context.visit(a, handler);

		Object[] expectedLogs = { "string", "integer", "b", "b.integer", "b.c", "b.c.dooble", "b.list", "b.list.0.dooble", "b.list.1.dooble", "b.list.2.dooble", "c", "c.dooble", "map",
				"map.key1.dooble", "map.key2.dooble" };
		Assertions.assertArrayEquals(expectedLogs, handler.getLogs());
	}
}

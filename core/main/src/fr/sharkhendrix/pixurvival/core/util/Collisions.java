package fr.sharkhendrix.pixurvival.core.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import fr.sharkhendrix.pixurvival.core.item.Items;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Collisions {

	public static boolean pointCircle(Vector2 c, Vector2 p, double r) {
		double dx = c.getX() - p.getX();
		double dy = c.getY() - p.getY();
		return dx * dx + dy * dy <= r * r;
	}

	public static boolean circleCircle(Vector2 center1, double radius1, Vector2 center2, double radius2) {
		double dx = center1.getX() - center2.getX();
		double dy = center1.getY() - center2.getY();
		double r = radius1 + radius2;
		return dx * dx + dy * dy <= r * r;
	}

	public static boolean dynamicCircleCircle(Vector2 center1, double radius1, Vector2 velocity1, Vector2 center2,
			double radius2) {
		Vector2 endPosition = new Vector2(center1).add(velocity1);
		Vector2 closestPoint = closestPointOnSegment(center1, endPosition, center2);

		return circleCircle(closestPoint, radius1, center2, radius2);
	}

	public static Vector2 closestPointOnSegment(Vector2 s1, Vector2 s2, Vector2 p) {
		double dx = s2.x - s1.x;
		double dy = s2.y - s1.y;

		if ((dx == 0) && (dy == 0)) {
			return new Vector2(s1);
		} else {
			double u = ((p.x - s1.x) * dx + (p.y - s1.y) * dy) / (dx * dx + dy * dy);
			if (u < 0) {
				return new Vector2(s1);
			} else if (u > 1) {
				return new Vector2(s2);
			} else {
				return new Vector2(s1.x + u * dx, s1.y + u * dy);
			}
		}
	}

	public static void main(String[] args) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Items.class);
		Items items = (Items) context.createUnmarshaller()
				.unmarshal(Collisions.class.getClassLoader().getResourceAsStream("items.xml"));
		System.out.println(items.get("truc"));
	}
}

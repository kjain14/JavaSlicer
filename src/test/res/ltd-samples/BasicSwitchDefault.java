package mytest;

public class BasicSwitchDefault {
	public static void main(String[] args) {
		int x = Integer.valueOf(args[0]);
		int y;
		switch (x % 3) {
			case 0:
				y = 10;
				break;
			case 1:
				y = 20;
				break;
			case 2:
				y = 30;
				break;
			default:
				y = -1;
				break;
		}
		System.out.println(y);
	}
}

package LuanVuEx;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Excercises Ex = new Excercises();
			TestingResult rs = new TestingResult();
			rs.setTc1(Ex.Excercise1());
			rs.setTc2(Ex.Excercise2());
			rs.setTc3(Ex.Excercise3());
			
			rs.generateHtml();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

import java.util.ArrayList;

import org.junit.Test;

public class DefaultTest {
	@Test
	public void arrayTest() {
		ArrayList<String> newStrArray=new ArrayList<>();
		newStrArray.add("-h");
		
		String[] strs =new String[newStrArray.size()];
		newStrArray.toArray(strs);
		System.out.println(strs);
		
	}

}

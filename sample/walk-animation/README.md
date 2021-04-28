# Walk Animation 

## Artwork
![AAA](https://github.com/VincentYeh-dev/IMG2PDF/blob/master/sample/walk-animation/image-sources/walka1.png?raw=true)

### credit
This drawing is credit by [kuma_fish](https://instagram.com/kuma_fishs?igshid=4o7rza34ha3p).

If you want to see more artworks that she drew, please check the link below.

[kuma_fish](https://instagram.com/kuma_fishs?igshid=4o7rza34ha3p)

## Get Started


**1. Get arguments from argument generator**
```java=

public class WalkAnimation {

	public static void main(String[] args) {
		File project_root = new File("").getAbsoluteFile().getParentFile().getParentFile();
		File sample_root = new File(project_root, "sample\\walk-animation");
		File taskslist_destination = new File(sample_root, "taskslist\\test.xml");
		taskslist_destination.delete();

		File image_sources_dir = new File(sample_root, "image-sources").getAbsoluteFile();

		File sources_list = new File(sample_root, "dirlist.txt").getAbsoluteFile();
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sources_list), "UTF-8"));
			writer.write(image_sources_dir.getAbsolutePath() + "\n\n");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String create_command = "-m create "
//				
				+ "-pz A4 "
//				
				+ "-ps NUMERTIC "
//				
				+ "-pa CENTER-CENTER "
//				
				+ "-pdi Vertical "
//				
				+ "-par YES "
//				
				+ "-pseq INCREASE "
//				
				+ "-pupwd 1234AAA "
//				
				+ "-popwd 1234AAA "
//				
				+ "-pp 11 "
//				
				+ "-f \"[^\\.]*\\.(png|PNG|jpg|JPG)\" "
//				
				+ "-pdst \"" + sample_root.getAbsolutePath() + "\\output\\$PARENT{0} $CY-$CM-$CD $CH-$CN-$CS.pdf\" "
//				
				+ "-ldst \"" + taskslist_destination.getAbsolutePath() + "\" -src \""
//				
				+ sources_list.getAbsolutePath() + "\"";

		String convert_command = "-m convert -o -lsrc \"" + taskslist_destination.getAbsolutePath()+"\"";
		System.out.println("create command:");
		System.out.println(create_command);
		
		System.out.println("convert command:");
		System.out.println(convert_command);
	}

```

2. Copy the arguments which the console printed.
```
create command:
-m create -pz A4 -ps NUMERTIC -pa CENTER-CENTER -pdi Vertical -par YES -pseq INCREASE -pupwd 1234AAA -popwd 1234AAA -pp 11 -f "[^\.]*\.(png|PNG|jpg|JPG)" -pdst "C:\Users\vince\git\IMG2PDF\sample\walk-animation\output\$PARENT{0} $CY-$CM-$CD $CH-$CN-$CS.pdf" -ldst "C:\Users\vince\git\IMG2PDF\sample\walk-animation\taskslist\test.xml" -src "C:\Users\vince\git\IMG2PDF\sample\walk-animation\dirlist.txt"
convert command:
-m convert -o -lsrc "C:\Users\vince\git\IMG2PDF\sample\walk-animation\taskslist\test.xml"
```

3. Run IMG2PDF

**Console**
```
java -jar IMG2PDF-0.0.1-SNAPSHOT -m create -pz A4 -ps NUMERTIC -pa CENTER-CENTER -pdi Vertical -par YES -pseq INCREASE -pupwd 1234AAA -popwd 1234AAA -pp 11 -f "[^\.]*\.(png|PNG|jpg|JPG)" -pdst "C:\Users\vince\git\IMG2PDF\sample\walk-animation\output\$PARENT{0} $CY-$CM-$CD $CH-$CN-$CS.pdf" -ldst "C:\Users\vince\git\IMG2PDF\sample\walk-animation\taskslist\test.xml" -src "C:\Users\vince\git\IMG2PDF\sample\walk-animation\dirlist.txt"

java -jar IMG2PDF-0.0.1-SNAPSHOT -m convert -o -lsrc "C:\Users\vince\git\IMG2PDF\sample\walk-animation\taskslist\test.xml"
```
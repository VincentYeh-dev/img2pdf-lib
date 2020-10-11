# Walk Animation 

## Artwork
![AAA](https://raw.githubusercontent.com/VincentYeh-dev/IMG2PDF/master/sample/sample-imgs/walk_animation/walka1.png)

### credit
This drawing is credit by [kuma_fish](https://instagram.com/kuma_fishs?igshid=4o7rza34ha3p).

If you want to see more artworks that she drew, please check the link below.

[kuma_fish](https://instagram.com/kuma_fishs?igshid=4o7rza34ha3p)

## Get Started

Please save dirlist.txt as UTF-8 encoding-type.

**dirlist.txt**
```
path\to\IMG2PDF\sample\walk-animation\image-sources
```

**java code**
```java=
String arguments="-sz A4 "
         + "-s NUMERTIC "
         + "-a CENTER|CENTER "
         + "-odr INCREASE "
         + "-lo tasklist.xml "
         + "-d $NAME.pdf "
         + "dirlist.txt";
		
        new TaskListCreator(arguments);
        new TaskProcessor("tasklist.xml");

```






# Walk Animation 

## Artwork
![AAA](https://github.com/VincentYeh-dev/IMG2PDF/blob/master/sample/walk-animation/image-sources/walka1.png?raw=true)

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






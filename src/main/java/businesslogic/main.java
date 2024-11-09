package businesslogic;

import configuration.ConfigXML;

public class main {

	public static void main(String[]	args)	{
	boolean isLocal =	true;
	ConfigXML co = ConfigXML.getInstance();
	BLFacade	blFacade =	new BLFactory(co).getBusinessLogicFactory(isLocal);
	ExtendedIterator<String>	i =	blFacade.getDepartingCitiesIterator();
	String c;
	System.out.println("_____________________");
	System.out.println("FROM	LAST	TO	FIRST");
	i.goLast();	//	Go	to	last	element
	while (i.hasPrevious())	{
	c =	i.previous();
	System.out.println(c);
	}
	System.out.println();
	System.out.println("_____________________");
	System.out.println("FROM	FIRST	TO	LAST");
	i.goFirst();	//	Go	to	first	element
	while (i.hasNext())	{
	c =	i.next();
	System.out.println(c);
	}
	}

}
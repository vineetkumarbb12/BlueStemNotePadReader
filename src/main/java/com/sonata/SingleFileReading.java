package com.sonata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
public class SingleFileReading 
{
    public static void main( String[] args )
    {
        //System.out.println( "Hello World!" );
        String textFilePath = "D:\\TextPadReader\\Notepad.txt";
        readTextFile(textFilePath);
    }
    
    public static void readTextFile(String textFilePath) {
		BufferedReader reader;

		try {
			reader = new BufferedReader(new FileReader(textFilePath));
			String line = reader.readLine();
			List<String> headers=Arrays.asList(line.split(","));
			while (line != null) {
				// read next line
				line = reader.readLine();
				if(line != null) {
					String[] data = line.split(",");
					for (int i = 0; i < data.length; i++) {
						System.out.println(headers.get(i) + " -- " + data[i]);
					}
				}
				System.out.println("************************");
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

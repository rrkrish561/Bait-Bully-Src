import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.net.*;

public class Testing {

   /*public static void main (String[] args) {
      /*Scanner scan = new Scanner(System.in);
      ArrayList<String> bruh = getTitles(scan.nextLine());
      for (String meme: bruh)   {
         System.out.println(meme);
      }

      urlToHTML("https://www.google.com/search?q=bruh&sxsrf=ALeKk01Xae2-U4-eOSISrDB7wRo5yOgh_Q:1582456444886&source=lnms&tbm=nws&sa=X&ved=2ahUKEwjEkZTSxefnAhWsiOAKHX4GCp0Q_AUoBHoECAwQBg&biw=1536&bih=722");

   }


   /*public static ArrayList<String> getTitles(String url) {
      String html = urlToHTML(url);
      ArrayList<String> bruh = new ArrayList<>();
      int begin = 0;
      int stringStart;
      for (int i = 0; i < 10; i++)   {
         stringStart = html.indexOf(html, begin);
         bruh.add(html.substring())
      }

      return null;
   }


   public static void urlToHTML(String urlt)  {
      URL url;

      try {
         // get URL content

         String a=urlt;
         url = new URL(a);
         URLConnection conn = url.openConnection();
         
        conn.addRequestProperty("User-Agent", "Mozilla");
        conn.setReadTimeout(500000);
        conn.setConnectTimeout(500000);

         // open the stream and put it into BufferedReader
         BufferedReader br = new BufferedReader(
                 new InputStreamReader(conn.getInputStream()));

         String inputLine;
         while ((inputLine = br.readLine()) != null) {
            System.out.println(inputLine);
         }
         br.close();

         System.out.println("Done");

      } catch (MalformedURLException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }


   }*/
   
   
   public static String getHTML(String urlToRead) throws Exception {
      StringBuilder result = new StringBuilder();
      URL url = new URL(urlToRead);
      
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.addRequestProperty("User-Agent", "Mozilla");
      conn.setReadTimeout(500000);
      conn.setConnectTimeout(500000);

      conn.setRequestMethod("GET");
      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;
      while ((line = rd.readLine()) != null) {
         result.append(line);
      }
      rd.close();
      return result.toString();
   }

   public static void main (String[] args) throws Exception{
      Scanner scan = new Scanner(System.in);
      ArrayList<String> bruh = getTitles("https://www.google.com/search?q=bruh&sxsrf=ALeKk01Xae2-U4-eOSISrDB7wRo5yOgh_Q:1582456444886&source=lnms&tbm=nws&sa=X&ved=2ahUKEwjEkZTSxefnAhWsiOAKHX4GCp0Q_AUoBHoECAwQBg&biw=1536&bih=722");
      for (String meme: bruh)   {
         System.out.println(meme);
      }

      //urlToHTML("https://www.google.com/search?q=bruh&sxsrf=ALeKk01Xae2-U4-eOSISrDB7wRo5yOgh_Q:1582456444886&source=lnms&tbm=nws&sa=X&ved=2ahUKEwjEkZTSxefnAhWsiOAKHX4GCp0Q_AUoBHoECAwQBg&biw=1536&bih=722");

   }
   
   
   public static ArrayList<String> getTitles(String url) throws Exception {
      String html = getHTML(url);
      ArrayList<String> bruh = new ArrayList<>();
      int begin = 0;
      int stringStart;
      String key = "BNeawe vvjwJb AP7Wnd";
      String title = "";
      for (int i = 0; i < 10; i++)   {
         stringStart = html.indexOf(key, begin) + 2 + key.length();
         title = html.substring(stringStart, html.indexOf("<", stringStart));
         while(title.contains("&#")) {
            //System.out.println(title.substring(title.indexOf("&#"), title.indexOf("&#") + 7 ));
            title = title.replaceAll(title.substring(title.indexOf("&#"), title.indexOf("&#") + 7 ), "'");
         }
         bruh.add(title);
         begin = stringStart;
      }
      return bruh;
   }



}
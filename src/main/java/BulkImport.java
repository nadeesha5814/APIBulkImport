/**
 * Created by NADEESHA on 2/26/2016.
 */

import org.apache.http.entity.mime.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;





public class BulkImport {
    static final String TRUST_STORE_URL_PROPERTY ="trust.store.url";
    static final String TRUST_STORE_PASSWORD_PROPERTY ="trust.store.password";
    static final String HOST_PROPERTY ="host";
    static final String PORT_PROPERTY ="port";
    static final String IMPORT_FOLDER_PROPERTY ="import.path";
    static final String ADMIN_USERID_PROPOERTY ="admin.userid";
    static final String ADMIN_PASSWORD_PROPERTY ="admin.password";
    static final String IMPORT_API_VERSION_PROPERTY ="import.api.version";
    static final String POST ="POST";
    static final String AUTHORIZATION_HTTP_HEADER ="Authorization";
    static final String BASIC_KEY ="Basic";
    static final String ZIP_KEY =".zip";
    static final String FILE_KEY ="file";
    static final String BOUNDARY_KEY ="---WSO2---";
    static final String CONNECTION_KEY ="Connection";
    static final String KEEPALIVE_KEY ="Keep-Alive";
    static final String CONTENT_LENGTH_KEY ="Content-length";
    static final String PRESERVE_PROVIDER_KEY ="preserveProvider";
    static final String EQUAL ="=";
    static final String PRESERVE_PROVIDER_PROPERTY ="preserve.provider";


    static Properties prop;
    static String authString;
    static int apiCount=0;
    static int successCount=0;


    public static void main (String[] args){
        ReadProperties();

        //SSL Cert
        String trustStore = prop.getProperty(TRUST_STORE_URL_PROPERTY);
        String trustStorePassword = prop.getProperty(TRUST_STORE_PASSWORD_PROPERTY);
        if(trustStore != null && !trustStore.isEmpty() && trustStorePassword != null && !trustStorePassword.isEmpty()) {
            System.setProperty("javax.net.ssl.trustStore", trustStore);
            System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
        }

        File folder = new File(prop.getProperty(IMPORT_FOLDER_PROPERTY));
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().toLowerCase().endsWith(ZIP_KEY)) {
                ImportAPI(listOfFiles[i]);
            }
        }
        // If no API's are found
        if (apiCount ==0){
            System.out.print("No zip files found!!!");
        } else{
            System.out.println("Total zip files found = "+apiCount);
            System.out.println("Successful API Imports = "+successCount);
        }

    }
    public static void ImportAPI (File api) {
        apiCount ++;
        System.out.println(" Adding API "+apiCount+"."+api.getName());
        byte[] b = new byte[(int) api.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(api);
            fileInputStream.read(b);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            FileBody fileBody = new FileBody(api); //image should be a String
            builder.addPart(FILE_KEY, fileBody);
            builder.setBoundary(BOUNDARY_KEY);
            URL url = new URL(prop.getProperty(HOST_PROPERTY) + ":" + prop.getProperty(PORT_PROPERTY) + "/api-import-export-" + prop.getProperty(IMPORT_API_VERSION_PROPERTY) + "/import-api?"+PRESERVE_PROVIDER_KEY+EQUAL+prop.getProperty(PRESERVE_PROVIDER_PROPERTY));
            HttpURLConnection e = (HttpURLConnection)url.openConnection();
            authString= encodeCredentials (prop.getProperty(ADMIN_USERID_PROPOERTY), prop.getProperty(ADMIN_PASSWORD_PROPERTY));
            e.setRequestProperty(AUTHORIZATION_HTTP_HEADER, BASIC_KEY + " " + authString);
            e.setDoOutput(true);
            e.setRequestMethod(POST);
            e.setRequestProperty(CONNECTION_KEY, KEEPALIVE_KEY);
            e.addRequestProperty(CONTENT_LENGTH_KEY, builder.build().getContentLength() + "");
            e.addRequestProperty(builder.build().getContentType().getName(), builder.build().getContentType().getValue());
            //e.addRequestProperty(fileBody.getContentType().getName(), reqEntity.getContentType().getValue());
            OutputStream os = e.getOutputStream();
            builder.build().writeTo(e.getOutputStream());
            os.close();
            e.connect();
            System.out.println("Status : " + e.getResponseCode() + " : " + e.getResponseMessage());
            //Getting a count of successful API imports
            if (e.getResponseCode()==201){
                successCount++;
            }
            System.out.println("---------------------------");




        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
            e.printStackTrace();
        } catch (IOException e1) {
            System.out.println("Error Reading The File.");
            e1.printStackTrace();
        }


    }






    public static void exportAPI (String name, String version, String provider){
             /* try {

            //SSL Cert
            String trustStore = "D:\\APIM-Demo\\API_M_1.10\\wso2am-1.10.0\\repository\\resources\\security\\wso2carbon.jks";
            String trustStorePassword = "wso2carbon";
            if(trustStore != null && !trustStore.isEmpty() && trustStorePassword != null && !trustStorePassword.isEmpty()) {
                System.setProperty("javax.net.ssl.trustStore", trustStore);
                System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
            }

                // Exporting API
                URL url = new URL(prop.getProperty(HOST_PROPERTY)+":"+prop.getProperty(PORT_PROPERTY)+"/api-import-export-"+prop.getProperty(IMPORT_API_VERSION_PROPERTY)+"/export-api?name="+name+"&version="+version+"&provider="+provider);
                HttpURLConnection e = (HttpURLConnection)url.openConnection();
                e.setDoOutput(true);
                e.setRequestMethod(POST);
                authString= encodeCredentials (prop.getProperty(ADMIN_USERID_PROPOERTY),prop.getProperty(ADMIN_PASSWORD_PROPERTY));
                e.setRequestProperty(AUTHORIZATION_HTTP_HEADER, BASIC_KEY + " "+authString);


                //Writing to file
                FileOutputStream fos = new FileOutputStream(prop.getProperty(IMPORT_FOLDER_PROPERTY)+name+ZIP_KEY);
                fos.write(ByteStreams.toByteArray(e.getInputStream()));
                fos.close();
            } catch (IOException error) {
                System.out.println("Error invoking API Export Service" + error);
            }
            */
    }

    public static void ReadProperties(){
        prop= new Properties();
        InputStream input = null;
        try {

            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);



        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static String encodeCredentials(String uid, String password){
        byte[] encodedBytes = Base64.encodeBase64((uid + ":" + password).getBytes());
        return new String(encodedBytes);

    }

}


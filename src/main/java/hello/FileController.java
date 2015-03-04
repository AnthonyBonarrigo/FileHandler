package hello;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileController {
	
    @RequestMapping(value="/upload", method=RequestMethod.GET)
    public @ResponseBody String provideUploadInfo() {
        return "You can upload a file by posting to this same URL.";
    }
    
    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload(@RequestParam("name") String name, 
            @RequestParam("file") MultipartFile file)
    {
        if (!file.isEmpty()) 
        {
            try 
            {
                byte[] bytes = file.getBytes();
                File theFile = new File(name);
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(theFile));
                stream.write(bytes);
                stream.close();
                return "You successfully uploaded " + name + "!";
            } catch (Exception e) 
            {
                return "You failed to upload " + name + " => " + e.getMessage();
            }
        //end if    
        } 
        else 
        {
            return "You failed to upload " + name + " because the file was empty.";
        }
    }
    
    @RequestMapping(value="/download", method=RequestMethod.GET)
	public @ResponseBody String provideDownloadInfo()
	{
		return "You can download a file from this URL + fileName.";
	}
	
	@RequestMapping(value="/download", method=RequestMethod.POST)
	public @ResponseBody String handleFileDownload(@RequestParam("name") String name, HttpServletResponse response)
	{
		//if file name is not empty
		if(name.length() > 0)
		{
			try 
			{
				File download = new File(name);
				InputStream inputStream = new FileInputStream(download);
				//force download
				response.setContentType("application/force-download");
				
				//create file name and append extension 
				response.setHeader("Content-Disposition", "attachment; filename="+ name + getFileExtension(download));
				
				//copy file to output stream
		        IOUtils.copy(inputStream, response.getOutputStream());
		        
		        response.flushBuffer();
		        inputStream.close();
		        return "Download sucessful!";
			} 
			catch (Exception e) 
			{
				return "File does not exist!";
			}
			
		}
		//File name is empty
		return "File name empty";
	}	
	
	
	public String getFileExtension(File file) {
	    String name = file.getName();
	    int lastIndexOf = name.lastIndexOf(".");
	    if (lastIndexOf != -1) {
	    	return name.substring(lastIndexOf);
	    }
	    return ""; //empty extension
	}
}
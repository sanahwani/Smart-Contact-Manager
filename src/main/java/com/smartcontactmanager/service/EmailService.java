package com.smartcontactmanager.service;


import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;

import javax.mail.internet.MimeMessage;

//using send email api here to send otp on registered email
@Service
public class EmailService {

    public boolean sendEmail(String subject, String message,String to){

      boolean  f =false;
        //use send email code here

        //responseble to sens email without attchmnt

            //variable for gmail
            String host="smtp.gmail.com";
            String from="wsanah911@gmail.com";

            //get the system props
            Properties properties=System.getProperties();
            System.out.println(properties);

            //set important info to properties object

            //host set
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", 465);
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");


            //STEP 1 : to get the session ------>we cant get obj of session directly. it has factory methd by whch we cn get its ibj

            Session session=Session.getInstance(properties, new Authenticator() { //needs props and authenticator

                //override this methd of passwrd authentctr
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("wsanah911@gmail.com","singlehood");
                }
            });
            session.setDebug(true);

            //	STEP2 :compose thr mesage(text,multimedia)
            MimeMessage m=new MimeMessage(session);

            try {

                //from mail
                m.setFrom(from);

                //adding recepient addeess
                m.addRecipient(Message.RecipientType.TO, new InternetAddress(to)); //type and address... also pass array of internet addresses whn u had to send mail to more thn 1 prsn


                //adding text to msg
                //   m.setText(message);
                m.setContent(message,"text/html");
                
                
                //adding subjct
                m.setSubject(subject);


                //send
                //STEP3 : SEND the msg using transport class
                Transport.send(m);

                System.out.println("msg sent successffully.....");
                f=true;

            }catch(Exception e) {
                e.printStackTrace();
            }
        return f;
        }
    }



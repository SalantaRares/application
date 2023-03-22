package com.application.utils.hibernate;


public class Messages {
    //not null input
    public static final String TO_MUCH_DATA_TIMEOUT = "Timpul de preluare date a fost depasit!";
    public static String OBJECT_NOT_NULL = "Obiectul introdus nu poate fi nul!";
    public static String ID_NOT_NULL = "Id-ul nu poate fi nul!";
    public static String CUSTOMER_NO_NOT_NULL = "CIF-ul nu poate fi nul!";
    public static String FILE_NOT_NULL = "Fisierul trebuie introdus!";
    public static String MOBILE_NOT_NULL = "Numarul de telefon trebuie completat!";
    public static String SEND_TIME_NOT_NULL = "Data trimiterii trebuie completata!";
    public static String SMS_ID_NOT_NULL = "ID-ul SMS-ului trebuie completat!";
    public static String SESSION_ID_NOT_NULL = "ID-ul sesiunii nu poate fi nul!";
    public static String MESSAGE_NOT_NULL = "Textul mesajului trebuie completat!";
    public static String MESSAGES_NOT_NULL = "Textul mesajului trebuie completat la toate inregistrarile!";
    public static String MOBILE_NUMBERS_NOT_NULL = "Numarul de telefon trebuie completat la toate inregistrarile!";
    public static String PARAMETER_NOT_FOUND = "Parametrul nu este completat";


    //not found
    public static String OBJECT_NOT_FOUND = "Obiectul nu a fost gasit in baza de date!";
    public static String NO_DATA_FOR_SESSION = "Nu exista date de procesat pentru sesiunea introdusa!";
    public static String SESSION_NOT_FOUND = "Sesiunea nu a putut fi identificata!";

    //invalid
    public static String INVALID_JOB_PROCESS_AFTER_HOUR = "Nu se mai pot trimite mesaje dupa ora 20!";
    public static String INVALID_SESS_ID_JOB_START = "Un job cu id-ul de sesiune: %s, este deja in lucru!";
    public static String INVALID_STARTED_JOB_NUBER = "Nu se poate starta un numar mai mare de %s job-uri!";
    public static String INVALID_SESSION_UPDATE = "Sesiunea nu se poate actualiza!";
    public static String INVALID_SESSION_DELETION = "Sesiunea nu se poate sterge";

    //error
    public static String HTTPS_CALL_ERROR = "Eroare la apelul catre resursa de trimitere sms!";
    public static String HTTPS_CALL_ERROR_CODE = "Eroare la extragerea codului de raspuns de la trimitere sms!";
    public static String HTTPS_CALL_ERROR_MESSAGE = "Eroare la extragerea continutului raspunsului apelului de trimitere sms!";
    public static String FILE_OUT_ERROR = "Eroare la extragerea fisierului din MultipartFile";
    public static String FILE_PROCESSING_ERROR = "Eroare la procesarea fisierului!";

    //info
    public static String ASYNC_EXCEL_PROCESS_RESPONSE = "Fisierul este in curs de procesare. Veti primi un mail de informare la terminarea procesarii.";
    public static String FILE_DATA_SAVED = "Datele din fisier au fost salvate!";
    public static String WRONG_FORMAT = "Format fisier invalid!Verificati formatul fisierului precum si denumirea acestuia ";
    //session review messages
    public  static String SESSION_STATE_ALREADY_SET = "Status-ul sesiunii este deja setat.";

    //Messages
    public static String CIF_CUI = "CIF,CUI";
    public static String VALUE_NOT_NULL = "Value cannot be null";
    public static String WRONG_FILENAME = "Nume fisier invalid. Numele fisierului trebuie sa contina la sfarsit numarul cererii. Va rugam redenumiti numele fisierului!";
}

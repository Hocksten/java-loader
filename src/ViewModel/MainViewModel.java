package ViewModel;


import com.sun.jna.platform.win32.WinBase;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainViewModel{
    private int PORT = 10001;
    private String Address = "IPAddress";
    private Socket socket = null;
    private String TLogin = "Login";
    private String Password = "Password";
    private String _login;
    private String _password;


    public MainViewModel() {
        Run();
    }

    private void Run() {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(Address, PORT));

            byte[] defaultSet = new byte[55];
            byte[] remdata = new byte[64];
            byte[] send = new byte[]{ 0x05, 0x00, 0x15, 0x0c, 0x00 };
            byte[] b = new byte[] { 0x1F, 0x00, 0x15, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

            OutputStream outStream = socket.getOutputStream();
            outStream.write(send);

            DataInputStream inStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));


            for (int i = 0; i < 7; i++) {
                remdata[i] = (byte)inStream.read();
            }

            System.out.println(bytesToHex(remdata));


            byte[] Login = TLogin.getBytes();
            for (int i = 0; i < TLogin.length(); i++) {
                b[i + 4] = Login[i];
                defaultSet[i + 6] = Login[i];
            }
            Login = Password.getBytes();
            for (int i = 0; i < Password.length(); i++) {
                b[i + 17] = Login[i];
            }

            for (int i = 4; i < 30; i++) {
                b[i] = (byte) (b[i] + remdata[4] + 1 ^ remdata[5] + 3);
            }
            outStream.write(b);

            for (int i = 0; i < 14; i++) {
                remdata[i] = (byte)inStream.read();
            }
            System.out.println(bytesToHex(remdata));

            if(remdata[4] == 0x00) {
                System.out.println("Login Sucess");
                defaultSet[19] = (byte) (remdata[5] ^ 0xAF);
                defaultSet[20] = (byte) (remdata[6] ^ 0xE0);
                defaultSet[21] = (byte) (remdata[7] ^ 0x65);
                defaultSet[22] = (byte) (remdata[8] ^ 0x6E);
                defaultSet[41] = (byte) (remdata[5] ^ 0x3A);
                defaultSet[42] = (byte) (remdata[6] ^ 0x18);
                defaultSet[43] = (byte) (remdata[7] ^ 0x9C);
                defaultSet[44] = (byte) (remdata[8] ^ 0x3A);
                defaultSet[45] = (byte) (remdata[10] ^ 0x18);
                defaultSet[46] = (byte) (remdata[11] ^ 0x9C);
                defaultSet[47] = (byte) (remdata[12] ^ 0x3A);
                defaultSet[48] = (byte) (remdata[13] ^ 0xC8);
                defaultSet[40] = (byte)0x4b;
                send = new byte[] { 0x08, 0x00, 0x15, 0x05, 0x00, 0x00, 0x00, 0x00 };
                outStream.write(send);

                for (int i = 0; i < 48; i++) {
                    remdata[i] = (byte)inStream.read();
                }
                System.out.println(bytesToHex(remdata));

                send = new byte[] { 0x06, 0x00, 0x15, 0x07, 0x00, 0x00 };
                outStream.write(send);
                for (int i = 0; i < 53; i++) {
                    remdata[i] = (byte)inStream.read();
                }
                System.out.println(bytesToHex(remdata));

                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 2; i < 13; i++) {
                    stringBuilder.append((char) remdata[i]);
                }
                System.out.println(stringBuilder);
                if(remdata[0] == 0x01) {
                    System.out.println("Server On");
                } else {
                    System.out.println("Server Off");
                }

                defaultSet[23] = (byte) (remdata[11] ^ 0xE6);
                defaultSet[24] = (byte) (remdata[12] ^ 0x22);
                defaultSet[25] = (byte) (remdata[13] ^ 0xCF);
                defaultSet[26] = (byte) (remdata[14] ^ 0xCF);
                defaultSet[27] = (byte) (remdata[15] ^ 0x6F);
                defaultSet[28] = (byte) (remdata[16] ^ 0xDE);
                defaultSet[29] = (byte) (remdata[17] ^ 0xBC);
                defaultSet[30] = (byte) (remdata[18] ^ 0x5B);
                defaultSet[31] = (byte) (remdata[19] ^ 0xDA);
                defaultSet[32] = (byte) (remdata[20] ^ 0x5E);
                defaultSet[33] = (byte) (remdata[21] ^ 0xDF);
                defaultSet[34] = (byte) (remdata[22] ^ 0xAC);
                defaultSet[35] = (byte) (remdata[23] ^ 0x37);
                defaultSet[36] = (byte) (remdata[24] ^ 0x1B);
                defaultSet[37] = (byte) (remdata[25] ^ 0xCD);
                defaultSet[38] = (byte) (remdata[26] ^ 0xBC);
                defaultSet[0] = (byte) (remdata[5] ^ 0x3A);
                defaultSet[1] = (byte) (remdata[6] ^ 0x4B);
                defaultSet[2] = (byte) (remdata[7] ^ 0x9C);
                defaultSet[3] = (byte) (remdata[8] ^ 0xCB);
                defaultSet[4] = (byte) (remdata[9] ^ 0xB6);
                defaultSet[5] = (byte) (remdata[10] ^ 0x4F);
                defaultSet[53] = (byte) (0xD0);
                defaultSet[54] = (byte) (0x32);

                FileOutputStream fos = new FileOutputStream("DefaultSet.tmp");
                fos.write(defaultSet);
                fos.close();

                System.out.println("File content:");
                for(int i=0; i<defaultSet.length;i++){
                    System.out.print((char)defaultSet[i]);
                }

                WinBase.PROCESS_INFORMATION pi = new WinBase.PROCESS_INFORMATION();
                Kernel32.STARTUPINFO si = new Kernel32.STARTUPINFO();
                si.cb = pi.size();
                ViewModel.Kernel32.INSTANCE.CreateProcess("RF_Online.bin",
                        null, null, null, false,
                        0, null, null, si, pi);
                socket.close();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String bytesToHex(byte[] array)
    {
        char[] val = new char[2*array.length];
        String hex = "0123456789ABCDEF";
        for (int i = 0; i < array.length; i++)
        {
            int b = array[i] & 0xff;
            val[2*i] = hex.charAt(b >>> 4);
            val[2*i + 1] = hex.charAt(b & 15);
        }
        return String.valueOf(val);
    }

    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    public String getTLogin() {
        return _login;
    }

    public void setTLogin(String TLogin) {
        this.TLogin = TLogin;
    }

    public String getPassword() {
        return _password;
    }

    public void setPassword(String TLogin) {
        this.TLogin = TLogin;
    }
}

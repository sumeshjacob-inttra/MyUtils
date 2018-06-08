package com.inttra;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class FtpReadWriteUtil
{
	private static final String		USER		= "xxxxxxxxxxxx";
	private static final String		SERVER		= "xxxxxxxxxxxxxxxxxxxxx";
	private static final Integer	PORT		= 22;
	private static final String		PASSWORD	= "xxxxxxxx";
	private static Session			session		= null;
	private static ChannelSftp		sftpChannel	= null;

	public static void main(String[] args) throws FileNotFoundException
	{
		JSch jsch = new JSch();
		try
		{
			session = jsch.getSession(USER, SERVER, PORT);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(PASSWORD);
			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();
			sftpChannel = (ChannelSftp) channel;

			String localPath = "C:\\Softwares\\Download";
			String serverPath = "/intarchive/carriers/CA2000/inbound_archive/323_IFTSAI/20180607";
			downLoadFiles(serverPath, localPath);
			copyFilesToServer(serverPath + "/test", localPath);
		}
		catch (JSchException e)
		{
			e.printStackTrace();
		}
		catch (SftpException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (sftpChannel != null)
				sftpChannel.exit();
			if (session != null)
				session.disconnect();
		}
	}

	private static void copyFilesToServer(String serverPath, String localPath) throws SftpException, FileNotFoundException
	{
		if (localPath != null && localPath.length() > 0)
		{
			File folder = new File(localPath);
			String[] files = null;

			if (folder.isDirectory())
			{
				files = folder.list();
			}
			else if (folder.isFile())
			{
				files = new String[1];
				files[0] = folder.getAbsolutePath();
			}

			for (String fileName : files)
			{
				File file = new File(fileName);
				if (file.isFile())
				{
					InputStream inputStream = new FileInputStream(file);
					File f = new File(file.getName());
					sftpChannel.put(inputStream, serverPath + f.getName());
				}
			}
		}

	}

	private static void downLoadFiles(String serverPath, String localPath) throws SftpException
	{
		localPath = "C:\\Softwares\\Download";
		serverPath = "/intarchive/carriers/CA2000/inbound_archive/323_IFTSAI/20180607";
		Vector<LsEntry> filelist = sftpChannel.ls(serverPath);
		if (filelist != null && !filelist.isEmpty() && sftpChannel != null && sftpChannel.isConnected())
		{
			for (LsEntry file : filelist)
			{
				if (!file.getAttrs().isDir())
				{
					sftpChannel.get(serverPath + "/" + file.getFilename(), localPath);
					System.out.println("DownLoaded file : " + file.getFilename() + " to " + localPath);
				}
			}
		}
	}
}

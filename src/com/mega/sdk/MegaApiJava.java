package com.mega.sdk;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class MegaApiJava
{
	MegaApi megaApi;
	MegaGfxProcessor gfxProcessor;
	static DelegateMegaLogger logger;
	
	void runCallback(Runnable runnable)
	{
		runnable.run();
	}
	
	static Set<DelegateMegaRequestListener> activeRequestListeners = Collections.synchronizedSet(new LinkedHashSet<DelegateMegaRequestListener>());
	static Set<DelegateMegaTransferListener> activeTransferListeners = Collections.synchronizedSet(new LinkedHashSet<DelegateMegaTransferListener>());
	static Set<DelegateMegaGlobalListener> activeGlobalListeners = Collections.synchronizedSet(new LinkedHashSet<DelegateMegaGlobalListener>());
	static Set<DelegateMegaListener> activeMegaListeners = Collections.synchronizedSet(new LinkedHashSet<DelegateMegaListener>());
	static Set<DelegateMegaTreeProcessor> activeMegaTreeProcessors = Collections.synchronizedSet(new LinkedHashSet<DelegateMegaTreeProcessor>());
	
	//Order options for getChildren
	public final static int ORDER_NONE = MegaApi.ORDER_NONE;
	public final static int ORDER_DEFAULT_ASC = MegaApi.ORDER_DEFAULT_ASC;
	public final static int ORDER_DEFAULT_DESC = MegaApi.ORDER_DEFAULT_DESC;
	public final static int ORDER_SIZE_ASC = MegaApi.ORDER_SIZE_ASC;
	public final static int ORDER_SIZE_DESC = MegaApi.ORDER_SIZE_DESC;
	public final static int ORDER_CREATION_ASC = MegaApi.ORDER_CREATION_ASC;
	public final static int ORDER_CREATION_DESC = MegaApi.ORDER_CREATION_DESC;
	public final static int ORDER_MODIFICATION_ASC = MegaApi.ORDER_MODIFICATION_ASC;
	public final static int ORDER_MODIFICATION_DESC = MegaApi.ORDER_MODIFICATION_DESC;
	public final static int ORDER_ALPHABETICAL_ASC = MegaApi.ORDER_ALPHABETICAL_ASC;
	public final static int ORDER_ALPHABETICAL_DESC = MegaApi.ORDER_ALPHABETICAL_DESC;
	
	public final static int LOG_LEVEL_FATAL = 0;
	public final static int LOG_LEVEL_ERROR = LOG_LEVEL_FATAL + 1;
	public final static int LOG_LEVEL_WARNING = LOG_LEVEL_ERROR + 1;
	public final static int LOG_LEVEL_INFO = LOG_LEVEL_WARNING + 1;
	public final static int LOG_LEVEL_DEBUG = LOG_LEVEL_INFO + 1;
	public final static int LOG_LEVEL_MAX = LOG_LEVEL_DEBUG + 1;
	
	public final static int EVENT_FEEDBACK = 0;
	public final static int EVENT_DEBUG = EVENT_FEEDBACK + 1;
	public final static int EVENT_INVALID = EVENT_DEBUG + 1;
	
	public MegaApiJava(String appKey, String basePath)
	{
		megaApi = new MegaApi(appKey, basePath);
	}
	
	public MegaApiJava(String appKey, String userAgent, String basePath, MegaGfxProcessor gfxProcessor)
	{
		this.gfxProcessor = gfxProcessor;
		megaApi = new MegaApi(appKey, gfxProcessor, basePath, userAgent);
	}
	
	public MegaApiJava(String appKey)
	{
		megaApi = new MegaApi(appKey);
	}
	
	/****************************************************************************************************/
	//LISTENER MANAGEMENT
	/****************************************************************************************************/
	public void addListener(MegaListenerInterface listener)
	{
		megaApi.addListener(createDelegateMegaListener(listener));
	}

	public void addRequestListener(MegaRequestListenerInterface listener)
	{
		megaApi.addRequestListener(createDelegateRequestListener(listener));
	}
	
	public void addTransferListener(MegaTransferListenerInterface listener)
	{
		megaApi.addTransferListener(createDelegateTransferListener(listener, false));
	}

	public void addGlobalListener(MegaGlobalListenerInterface listener)
	{
		megaApi.addGlobalListener(createDelegateGlobalListener(listener));
	}

	public void removeListener(MegaListenerInterface listener)
	{
		synchronized(activeMegaListeners)
		{
			Iterator<DelegateMegaListener> it = activeMegaListeners.iterator();
			while(it.hasNext())
			{
				DelegateMegaListener delegate = it.next();
				if(delegate.getUserListener()==listener)
				{
					megaApi.removeListener(delegate);
					it.remove();
				}
			}
		}		
	}

	public void removeRequestListener(MegaRequestListenerInterface listener)
	{
		synchronized(activeRequestListeners)
		{
			Iterator<DelegateMegaRequestListener> it = activeRequestListeners.iterator();
			while(it.hasNext())
			{
				DelegateMegaRequestListener delegate = it.next();
				if(delegate.getUserListener()==listener)
				{	
					megaApi.removeRequestListener(delegate);
					it.remove();
				}
			}
		}
	}

	public void removeTransferListener(MegaTransferListenerInterface listener)
	{
		synchronized(activeTransferListeners)
		{
			Iterator<DelegateMegaTransferListener> it = activeTransferListeners.iterator();
			while(it.hasNext())
			{
				DelegateMegaTransferListener delegate = it.next();
				if(delegate.getUserListener()==listener)
				{
					megaApi.removeTransferListener(delegate);
					it.remove();
				}
			}
		}
	}

	public void removeGlobalListener(MegaGlobalListenerInterface listener)
	{
		synchronized(activeGlobalListeners)
		{
			Iterator<DelegateMegaGlobalListener> it = activeGlobalListeners.iterator();
			while(it.hasNext())
			{
				DelegateMegaGlobalListener delegate = it.next();
				if(delegate.getUserListener()==listener)
				{
					megaApi.removeGlobalListener(delegate);
					it.remove();
				}
			}
		}		
	}

	/****************************************************************************************************/
	//UTILS
	/****************************************************************************************************/
	public String getBase64PwKey(String password)
	{
		return megaApi.getBase64PwKey(password);
	}

	public String getStringHash(String base64pwkey, String inBuf)
	{
		return megaApi.getStringHash(base64pwkey, inBuf);
	}

	public static long base64ToHandle(String base64Handle)
	{
		return MegaApi.base64ToHandle(base64Handle);
	}

	public void retryPendingConnections()
	{
		megaApi.retryPendingConnections();
	}

	
	/****************************************************************************************************/
	//REQUESTS
	/****************************************************************************************************/
	public void login(String email, String password, MegaRequestListenerInterface listener)
	{
		megaApi.login(email, password, createDelegateRequestListener(listener));
	}

	public void login(String email, String password)
	{
		megaApi.login(email, password);
	}
	
	public String dumpSession() {
		return megaApi.dumpSession();
	}

	public void fastLogin(String email, String stringHash, String base64pwkey, MegaRequestListenerInterface listener)
	{
		megaApi.fastLogin(email, stringHash, base64pwkey, createDelegateRequestListener(listener));
	}

	public void fastLogin(String email, String stringHash, String base64pwkey)
	{
		megaApi.fastLogin(email, stringHash, base64pwkey);
	}
	
	public void fastLogin(String session, MegaRequestListenerInterface listener)
	{
		megaApi.fastLogin(session, createDelegateRequestListener(listener));
	}

	public void fastLogin(String session)
	{
		megaApi.fastLogin(session);
	}
	
	public void createAccount(String email, String password, String name, MegaRequestListenerInterface listener)
	{
		megaApi.createAccount(email, password, name, createDelegateRequestListener(listener));
	}

	public void createAccount(String email, String password, String name)
	{
		megaApi.createAccount(email, password, name);
	}

	public void fastCreateAccount(String email, String base64pwkey, String name, MegaRequestListenerInterface listener)
	{
		megaApi.fastCreateAccount(email, base64pwkey, name, createDelegateRequestListener(listener));
	}

	public void fastCreateAccount(String email, String base64pwkey, String name)
	{
		megaApi.fastCreateAccount(email, base64pwkey, name);
	}

	public void querySignupLink(String link, MegaRequestListenerInterface listener)
	{
		megaApi.querySignupLink(link, createDelegateRequestListener(listener));
	}

	public void querySignupLink(String link)
	{
		megaApi.querySignupLink(link);
	}

	public void confirmAccount(String link, String password, MegaRequestListenerInterface listener)
	{
		megaApi.confirmAccount(link, password, createDelegateRequestListener(listener));
	}

	public void confirmAccount(String link, String password)
	{
		megaApi.confirmAccount(link, password);
	}

	public void fastConfirmAccount(String link, String base64pwkey, MegaRequestListenerInterface listener)
	{
		megaApi.fastConfirmAccount(link, base64pwkey, createDelegateRequestListener(listener));
	}

	public void fastConfirmAccount(String link, String base64pwkey)
	{
		megaApi.fastConfirmAccount(link, base64pwkey);
	}

	public int isLoggedIn()
	{
		return megaApi.isLoggedIn();
	}

	public String getMyEmail()
	{
		return megaApi.getMyEmail();
	}
	
	public static void setLogLevel(int logLevel)
	{
		MegaApi.setLogLevel(logLevel);
	}
	
	public static void setLoggerObject(MegaLoggerInterface megaLogger)
	{
		DelegateMegaLogger newLogger = new DelegateMegaLogger(megaLogger);
		MegaApi.setLoggerObject(newLogger);
		logger = newLogger;
	}
	
	public static void log(int logLevel, String message, String filename, int line)
	{
		MegaApi.log(logLevel, message, filename, line);
	}
	
	public static void log(int logLevel, String message, String filename)
	{
		MegaApi.log(logLevel, message, filename);
	}
	
	public static void log(int logLevel, String message)
	{
		MegaApi.log(logLevel, message);
	}

	public void createFolder(String name, MegaNode parent, MegaRequestListenerInterface listener)
	{
		megaApi.createFolder(name, parent, createDelegateRequestListener(listener));
	}

	public void createFolder(String name, MegaNode parent)
	{
		megaApi.createFolder(name, parent);
	}

	public void moveNode(MegaNode node, MegaNode newParent, MegaRequestListenerInterface listener)
	{
		megaApi.moveNode(node, newParent, createDelegateRequestListener(listener));
	}

	public void moveNode(MegaNode node, MegaNode newParent)
	{
		megaApi.moveNode(node, newParent);
	}

	public void copyNode(MegaNode node, MegaNode newParent, MegaRequestListenerInterface listener)
	{
		megaApi.copyNode(node, newParent, createDelegateRequestListener(listener));
	}

	public void copyNode(MegaNode node, MegaNode newParent)
	{
		megaApi.copyNode(node, newParent);
	}

	public void renameNode(MegaNode node, String newName, MegaRequestListenerInterface listener)
	{
		megaApi.renameNode(node, newName, createDelegateRequestListener(listener));
	}

	public void renameNode(MegaNode node, String newName)
	{
		megaApi.renameNode(node, newName);
	}

	public void remove(MegaNode node, MegaRequestListenerInterface listener)
	{
		megaApi.remove(node, createDelegateRequestListener(listener));
	}

	public void remove(MegaNode node)
	{
		megaApi.remove(node);
	}
	
	public void sendFileToUser(MegaNode node, MegaUser user, MegaRequestListenerInterface listener)
	{
		megaApi.sendFileToUser(node, user, createDelegateRequestListener(listener));
	}

	public void sendFileToUser(MegaNode node, MegaUser user)
	{
		megaApi.sendFileToUser(node, user);
	}

	public void share(MegaNode node, MegaUser user, int level, MegaRequestListenerInterface listener)
	{
		megaApi.share(node, user, level, createDelegateRequestListener(listener));
	}

	public void share(MegaNode node, MegaUser user, int level)
	{
		megaApi.share(node, user, level);
	}

	public void loginToFolder(String megaFolderLink, MegaRequestListenerInterface listener)
	{
		megaApi.loginToFolder(megaFolderLink, createDelegateRequestListener(listener));
	}

	public void loginToFolder(String megaFolderLink)
	{
		megaApi.loginToFolder(megaFolderLink);
	}

	public void importFileLink(String megaFileLink, MegaNode parent, MegaRequestListenerInterface listener)
	{
		megaApi.importFileLink(megaFileLink, parent, createDelegateRequestListener(listener));
	}

	public void importFileLink(String megaFileLink, MegaNode parent)
	{
		megaApi.importFileLink(megaFileLink, parent);
	}

	public void getPublicNode(String megaFileLink, MegaRequestListenerInterface listener)
	{
		megaApi.getPublicNode(megaFileLink, createDelegateRequestListener(listener));
	}

	public void getPublicNode(String megaFileLink)
	{
		megaApi.getPublicNode(megaFileLink);
	}

	public void getThumbnail(MegaNode node, String dstFilePath, MegaRequestListenerInterface listener)
	{
		megaApi.getThumbnail(node, dstFilePath, createDelegateRequestListener(listener));
	}

	public void getThumbnail(MegaNode node, String dstFilePath)
	{
		megaApi.getThumbnail(node, dstFilePath);
	}

	public void setThumbnail(MegaNode node, String srcFilePath, MegaRequestListenerInterface listener)
	{
		megaApi.setThumbnail(node, srcFilePath, createDelegateRequestListener(listener));
	}

	public void setThumbnail(MegaNode node, String srcFilePath)
	{
		megaApi.setThumbnail(node, srcFilePath);
	}
	
	public void getPreview(MegaNode node, String dstFilePath, MegaRequestListenerInterface listener)
	{
		megaApi.getPreview(node, dstFilePath, createDelegateRequestListener(listener));
	}
	
	public void getPreview(MegaNode node, String dstFilePath)
	{
		megaApi.getPreview(node, dstFilePath);
	}
	
	public void setPreview(MegaNode node, String srcFilePath, MegaRequestListenerInterface listener)
	{
		megaApi.setPreview(node, srcFilePath, createDelegateRequestListener(listener));
	}
	
	public void setPreview(MegaNode node, String srcFilePath)
	{
		megaApi.setPreview(node, srcFilePath);
	}
	
	public void getUserAvatar(MegaUser user, String dstFilePath, MegaRequestListenerInterface listener)
	{
		megaApi.getUserAvatar(user, dstFilePath, createDelegateRequestListener(listener));
	}
	
	public void getUserAvatar(MegaUser user, String dstFilePath)
	{
		megaApi.getUserAvatar(user, dstFilePath);
	}
	  
	public void exportNode(MegaNode node, MegaRequestListenerInterface listener)
	{
		megaApi.exportNode(node, createDelegateRequestListener(listener));
	}

	public void exportNode(MegaNode node)
	{
		megaApi.exportNode(node);
	}
	
	public void disableExport(MegaNode node, MegaRequestListenerInterface listener)
	{
		megaApi.disableExport(node, createDelegateRequestListener(listener));
	}
	
	public void disableExport(MegaNode node)
	{
		megaApi.disableExport(node);
	}

	public void fetchNodes(MegaRequestListenerInterface listener)
	{
		megaApi.fetchNodes(createDelegateRequestListener(listener));
	}

	public void fetchNodes()
	{
		megaApi.fetchNodes();
	}
	
	public void getAccountDetails(MegaRequestListenerInterface listener)
	{
		megaApi.getAccountDetails(createDelegateRequestListener(listener));
	}

	public void getAccountDetails()
	{
		megaApi.getAccountDetails();
	}
	
	public void getPricing(MegaRequestListenerInterface listener) 
	{
	    megaApi.getPricing(createDelegateRequestListener(listener));
	}

	public void getPricing() 
	{
		megaApi.getPricing();
	}

	public void getPaymentUrl(long productHandle, MegaRequestListenerInterface listener) 
	{
		megaApi.getPaymentUrl(productHandle, createDelegateRequestListener(listener));
	}

	public void getPaymentUrl(long productHandle) 
	{
		megaApi.getPaymentUrl(productHandle);
	}

	public String exportMasterKey() 
	{
		return megaApi.exportMasterKey();
	}
	
	public void changePassword(String oldPassword, String newPassword, MegaRequestListenerInterface listener)
	{
		megaApi.changePassword(oldPassword, newPassword, createDelegateRequestListener(listener));
	}

	public void changePassword(String oldPassword, String newPassword)
	{
		megaApi.changePassword(oldPassword, newPassword);
	}

	public void logout(MegaRequestListenerInterface listener)
	{
		megaApi.logout(createDelegateRequestListener(listener));
	}

	public void addContact(String email, MegaRequestListenerInterface listener)
	{
		megaApi.addContact(email, createDelegateRequestListener(listener));
	}

	public void addContact(String email)
	{
		megaApi.addContact(email);
	}

	public void logout()
	{
		megaApi.logout();
	}
	
	public void submitFeedback(int rating, String comment, MegaRequestListenerInterface listener) 
	{
		megaApi.submitFeedback(rating, comment, createDelegateRequestListener(listener));
	}

	public void submitFeedback(int rating, String comment) 
	{
		megaApi.submitFeedback(rating, comment);
	}

	public void reportDebugEvent(String text, MegaRequestListenerInterface listener) 
	{
		megaApi.reportDebugEvent(text, createDelegateRequestListener(listener));
	}

	public void reportDebugEvent(String text) 
	{
		megaApi.reportDebugEvent(text);
	}

	/****************************************************************************************************/
	//TRANSFERS
	/****************************************************************************************************/
	public void startUpload(String localPath, MegaNode parent, MegaTransferListenerInterface listener)
	{
		megaApi.startUpload(localPath, parent, createDelegateTransferListener(listener));
	}

	public void startUpload(String localPath, MegaNode parent)
	{
		megaApi.startUpload(localPath, parent);
	}

	public void startUpload(String localPath, MegaNode parent, String fileName, MegaTransferListenerInterface listener)
	{
		megaApi.startUpload(localPath, parent, fileName, createDelegateTransferListener(listener));
	}

	public void startUpload(String localPath, MegaNode parent, String fileName)
	{
		megaApi.startUpload(localPath, parent, fileName);
	}

	public void startDownload(MegaNode node, String localFolder, MegaTransferListenerInterface listener)
	{
		megaApi.startDownload(node, localFolder, createDelegateTransferListener(listener));
	}

	public void startDownload(MegaNode node, String localFolder)
	{
		megaApi.startDownload(node, localFolder);
	}
	
	public void cancelTransfer(MegaTransfer transfer, MegaRequestListenerInterface listener)
	{
		megaApi.cancelTransfer(transfer, createDelegateRequestListener(listener));
	}

	public void cancelTransfer(MegaTransfer transfer)
	{
		megaApi.cancelTransfer(transfer);
	}

	public void cancelTransfers(int direction, MegaRequestListenerInterface listener)
	{
		megaApi.cancelTransfers(direction, createDelegateRequestListener(listener));
	}

	public void cancelTransfers(int direction)
	{
		megaApi.cancelTransfers(direction);
	}

	public void pauseTransfers(boolean pause, MegaRequestListenerInterface listener)
	{
		megaApi.pauseTransfers(pause, createDelegateRequestListener(listener));
	}

	public void pauseTransfers(boolean pause)
	{
		megaApi.pauseTransfers(pause);
	}

	public void setUploadLimit(int bpslimit)
	{
		megaApi.setUploadLimit(bpslimit);
	}
		  
	public ArrayList<MegaTransfer> getTransfers()
	{
		return transferListToArray(megaApi.getTransfers());
	}
	
	public int getNumPendingDownloads()
	{
		return megaApi.getNumPendingDownloads();
	}
	
	public int getNumPendingUploads()
	{
		return megaApi.getNumPendingUploads();
	}
	
	public int getTotalDownloads()
	{
		return megaApi.getTotalDownloads();
	}
	
	public int getTotalUploads()
	{
		return megaApi.getTotalUploads();
	}
	
	public void resetTotalDownloads()
	{
		megaApi.resetTotalDownloads();
	}
	
	public void resetTotalUploads()
	{
		megaApi.resetTotalUploads();
	}
	
	public void startStreaming(MegaNode node, long startOffset, long size, MegaTransferListenerInterface listener)
	{
		megaApi.startStreaming(node, startOffset, size, createDelegateTransferListener(listener));
	}
	
	public void startUnbufferedDownload(MegaNode node, long startOffset, long size, OutputStream outputStream, MegaTransferListenerInterface listener)
	{
		DelegateMegaTransferListener delegateListener = new DelegateOutputMegaTransferListener(this, outputStream, listener, true);
		activeTransferListeners.add(delegateListener);
		megaApi.startStreaming(node, startOffset, size, delegateListener);
	}
	
	public void startUnbufferedDownload(MegaNode node, OutputStream outputStream, MegaTransferListenerInterface listener)
	{
		startUnbufferedDownload(node, 0, node.getSize(), outputStream, listener);
	}
	
	/****************************************************************************************************/
	//FILESYSTEM METHODS
	/****************************************************************************************************/
	public ArrayList<MegaNode> getChildren(MegaNode parent, int order)
	{
		return nodeListToArray(megaApi.getChildren(parent, order));
	}

	public ArrayList<MegaNode> getChildren(MegaNode parent)
	{
		return nodeListToArray(megaApi.getChildren(parent));
	}
	
	public int getNumChildren(MegaNode parent) 
	{
		return megaApi.getNumChildren(parent);
	}
	
	public int getNumChildFiles(MegaNode parent) 
	{
		return megaApi.getNumChildFiles(parent);
	}

	public int getNumChildFolders(MegaNode parent) 
	{
	  return megaApi.getNumChildFolders(parent);
	}
	
	public MegaNode getChildNode(MegaNode parent, String name)
	{
		return megaApi.getChildNode(parent, name);
	}

	public MegaNode getParentNode(MegaNode node)
	{
		return megaApi.getParentNode(node);
	}

	public String getNodePath(MegaNode node)
	{
		return megaApi.getNodePath(node);
	}

	public MegaNode getNodeByPath(String path, MegaNode baseFolder)
	{
		return megaApi.getNodeByPath(path, baseFolder);
	}

	public MegaNode getNodeByPath(String path)
	{
		return megaApi.getNodeByPath(path);
	}

	public MegaNode getNodeByHandle(long handle)
	{
		return megaApi.getNodeByHandle(handle);
	}
	
	public ArrayList<MegaUser> getContacts() 
	{
		return userListToArray(megaApi.getContacts());
	}

	public MegaUser getContact(String email) 
	{
		return megaApi.getContact(email);
	}
	
	public ArrayList<MegaNode> getInShares(MegaUser user)
	{
		return nodeListToArray(megaApi.getInShares(user));
	}

	public ArrayList<MegaNode> getInShares()
	{
		return nodeListToArray(megaApi.getInShares());
	}
	
	public boolean isShared(MegaNode node) 
	{
		return megaApi.isShared(node);
	}
	
	public ArrayList<MegaShare> getOutShares()
	{
		return shareListToArray(megaApi.getOutShares());
	}
	
	public ArrayList<MegaShare> getOutShares(MegaNode node)
	{
		return shareListToArray(megaApi.getOutShares(node));		
	}
	
	public int getAccess(MegaNode node)
	{
		return megaApi.getAccess(node);
	}

	public String getFingerprint(String filePath) 
	{
	    return megaApi.getFingerprint(filePath);
	}

	public String getFingerprint(MegaNode node) 
	{
		return megaApi.getFingerprint(node);
	}

	public MegaNode getNodeByFingerprint(String fingerprint) 
	{
	    return megaApi.getNodeByFingerprint(fingerprint);
	}

	public boolean hasFingerprint(String fingerprint) 
	{
		return megaApi.hasFingerprint(fingerprint);
	}
		  
	public MegaError checkAccess(MegaNode node, int level)
	{
		return megaApi.checkAccess(node, level);
	}

	public MegaError checkMove(MegaNode node, MegaNode target)
	{
		return megaApi.checkMove(node, target);
	}

	public MegaNode getRootNode()
	{
		return megaApi.getRootNode();
	}

	public MegaNode getInboxNode()
	{
		return megaApi.getInboxNode();
	}

	public MegaNode getRubbishNode()
	{
		return megaApi.getRubbishNode();
	}
	
	public ArrayList<MegaNode> search(MegaNode parent, String searchString, boolean recursive)
	{
		return nodeListToArray(megaApi.search(parent, searchString, recursive));
	}
	
	public boolean processMegaTree(MegaNode parent, MegaTreeProcessorInterface processor, boolean recursive)
	{
		DelegateMegaTreeProcessor delegateListener = new DelegateMegaTreeProcessor(this, processor);
		activeMegaTreeProcessors.add(delegateListener);
		boolean result = megaApi.processMegaTree(parent, delegateListener, recursive);
		activeMegaTreeProcessors.remove(delegateListener);
		return result;
	}
	
	/****************************************************************************************************/
	//INTERNAL METHODS
	/****************************************************************************************************/
	
	private MegaRequestListener createDelegateRequestListener(MegaRequestListenerInterface listener)
	{
		DelegateMegaRequestListener delegateListener = new DelegateMegaRequestListener(this, listener);
		activeRequestListeners.add(delegateListener);
		return delegateListener;
	}
	
	private MegaTransferListener createDelegateTransferListener(MegaTransferListenerInterface listener)
	{
		DelegateMegaTransferListener delegateListener = new DelegateMegaTransferListener(this, listener, true);
		activeTransferListeners.add(delegateListener);
		return delegateListener;
	}
	
	private MegaTransferListener createDelegateTransferListener(MegaTransferListenerInterface listener, boolean singleListener)
	{
		DelegateMegaTransferListener delegateListener = new DelegateMegaTransferListener(this, listener, singleListener);
		activeTransferListeners.add(delegateListener);
		return delegateListener;
	}
	
	private MegaGlobalListener createDelegateGlobalListener(MegaGlobalListenerInterface listener)
	{
		DelegateMegaGlobalListener delegateListener = new DelegateMegaGlobalListener(this, listener);
		activeGlobalListeners.add(delegateListener);
		return delegateListener;
	}
	
	private MegaListener createDelegateMegaListener(MegaListenerInterface listener)
	{
		DelegateMegaListener delegateListener = new DelegateMegaListener(this, listener);
		activeMegaListeners.add(delegateListener);
		return delegateListener;
	}
	
	void privateFreeRequestListener(DelegateMegaRequestListener listener)
	{
		activeRequestListeners.remove(listener);
	}
	
	void privateFreeTransferListener(DelegateMegaTransferListener listener)
	{
		activeTransferListeners.remove(listener);
	}
	
	static ArrayList<MegaNode> nodeListToArray(MegaNodeList nodeList)
	{
		if (nodeList == null)
		{
			return null;
		}
		
		ArrayList<MegaNode> result = new ArrayList<MegaNode>(nodeList.size());
		for(int i=0; i<nodeList.size(); i++)
		{
			result.add(nodeList.get(i).copy());
		}
		
		return result;
	}
	
	static ArrayList<MegaShare> shareListToArray(MegaShareList shareList)
	{
		if (shareList == null)
		{
			return null;
		}
		
		ArrayList<MegaShare> result = new ArrayList<MegaShare>(shareList.size());
		for(int i=0; i<shareList.size(); i++)
		{
			result.add(shareList.get(i).copy());
		}
		
		return result;
	}
	
	static ArrayList<MegaTransfer> transferListToArray(MegaTransferList transferList)
	{
		if (transferList == null)
		{
			return null;
		}
		
		ArrayList<MegaTransfer> result = new ArrayList<MegaTransfer>(transferList.size());
		for(int i=0; i<transferList.size(); i++)
		{
			result.add(transferList.get(i).copy());
		}
		
		return result;
	}
	
	static ArrayList<MegaUser> userListToArray(MegaUserList userList)
	{
		
		if (userList == null)
		{
			return null;
		}
		
		ArrayList<MegaUser> result = new ArrayList<MegaUser>(userList.size());
		for(int i=0; i<userList.size(); i++)
		{
			result.add(userList.get(i).copy());
		}
		
		return result;
	}
}

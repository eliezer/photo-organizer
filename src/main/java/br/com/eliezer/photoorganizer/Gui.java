package br.com.eliezer.photoorganizer;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;


public class Gui {

	private static final String DONE_TEXT = "Concluido";
	private static final String START_TEXT = "Iniciar";
	protected Shell shell;
	protected List<String> listFilesDroped;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Gui window = new Gui();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		final Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell(SWT.CLOSE);
		shell.setImage(SWTResourceManager.getImage(Gui.class, "/photos-icon.png"));
		shell.setSize(236, 258);
		shell.setText("Organizador de Fotos");
		final FillLayout fillLayoutShell = new FillLayout(SWT.HORIZONTAL);
		fillLayoutShell.marginWidth = 10;
		fillLayoutShell.marginHeight = 10;
		shell.setLayout(fillLayoutShell);

		final Composite mainComposite = new Composite(shell, SWT.NONE);
		mainComposite.setLayout(new FormLayout());

		final Composite dropComposite = new Composite(mainComposite, SWT.NONE);
		FormData fd_dropComposite = new FormData();
		fd_dropComposite.bottom = new FormAttachment(0, 177);
		fd_dropComposite.right = new FormAttachment(0, 212);
		fd_dropComposite.top = new FormAttachment(0);
		fd_dropComposite.left = new FormAttachment(0);
		dropComposite.setLayoutData(fd_dropComposite);
		dropComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

		final DropTarget dropTarget = new DropTarget(dropComposite, DND.DROP_DEFAULT | DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		
		final CLabel labelImageDrop = new CLabel(dropComposite, SWT.NONE);
		labelImageDrop.setBackground(SWTResourceManager.getImage(Gui.class, "/drop-here.png"));
		labelImageDrop.setText("");
		
		final Composite butonsComposite = new Composite(mainComposite, SWT.NONE);
		FormData fd_butonsComposite = new FormData();
		fd_butonsComposite.bottom = new FormAttachment(100);
		fd_butonsComposite.top = new FormAttachment(0, 177);
		fd_butonsComposite.left = new FormAttachment(0);
		butonsComposite.setLayoutData(fd_butonsComposite);
		butonsComposite.setLayout(new GridLayout(2, false));
		
		final ProgressBar progressBar = new ProgressBar(butonsComposite, SWT.NONE);
		GridData gd_progressBar = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_progressBar.widthHint = 126;
		progressBar.setLayoutData(gd_progressBar);
		
		final Button btnStart = new Button(butonsComposite, SWT.NONE);
		GridData gd_btnStart = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnStart.widthHint = 71;
		btnStart.setLayoutData(gd_btnStart);
		btnStart.setSize(new Point(40, 10));
		btnStart.setEnabled(false);
		btnStart.setText(START_TEXT);
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				btnStart.setEnabled(false);
				progressBar.setMaximum(listFilesDroped.size());
				for (int i=0; i<listFilesDroped.size();i++) {
					File currentFile = new File(listFilesDroped.get(i));
					try {
						PhotoOrganizer.moveFile(currentFile);
						progressBar.setSelection(i+1);
					} catch (Exception e1) {
						messageError(String.format("Erro ao processar arquivo: %s \nERRO: %s", currentFile.toString(), e1));
						e1.printStackTrace();
						return;
					}
				}
				btnStart.setText(DONE_TEXT);
			}

			private void messageError(String msg) {
				final MessageBox dialog = new MessageBox(shell, SWT.ICON_ERROR);
				dialog.setText("Ops");
				dialog.setMessage(msg);
				dialog.open();
			}
		});
		
		dropTarget.addDropListener(new DropTargetAdapter() {
			public void drop(DropTargetEvent event) {
				listFilesDroped = new ArrayList<String>();
				final FileTransfer fileTransfer = FileTransfer.getInstance();
				if (fileTransfer.isSupportedType(event.currentDataType)) {
					listFilesDroped = Arrays.asList((String[]) event.data);
				}
				if(!listFilesDroped.isEmpty()){
					btnStart.setEnabled(true);
					btnStart.setText(START_TEXT);
				}
			}
		});
	}
}

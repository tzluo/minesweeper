import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class bomb extends ApplicationWindow {
	private Action action_restart;
	private Action action_setting;
	
	int length = 20;
	double diff = 0.1;

	boolean[][] IsBomb = new boolean[length][length];
	int[][] btnStatus = new int[length][length];
	int[][] bombcount = new int[length][length];
	boolean lostgame = false;
	int[] btninfocus = {-1,-1};
	Canvas Board;


	public void BombInit() {
		lostgame = false;
		btninfocus[0]=-1;btninfocus[1]=-1;
		IsBomb= new boolean[length][length];
		btnStatus = new int[length][length];
		bombcount = new int[length][length];
		getShell().setSize(67+20*length, 137+20*length);
		getShell().setLocation((int)(1024-(67+20*length))/2, (int)(768-(137+20*length))/2);
		Board.setSize(20*length, 20*length);
		
		for (int i = 0; i < length; i++)
			for (int j = 0; j < length; j++) 
			{
				IsBomb[i][j] = Math.random() > diff ? false : true;
				btnStatus[i][j] = 0;
			}
		Board.redraw();

		for (int i = 0; i < length; i++)
			for (int j = 0; j < length; j++)
			{
				int count = 0;
				for (int a = -1; a <= 1; a++)
					for (int b = -1; b <= 1; b++)
						if (!(a == 0 && b == 0) && i + a >= 0 && i + a < length	&& j + b >= 0 && j + b < length)
							count += IsBomb[i + a][j + b] == true ? 1 : 0;
				bombcount[i][j] = count;
			}
	}

	/**
	 * Create the application window.
	 */
	public bomb() {
		super(null);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
	}

	/**
	 * Create contents of the application window.
	 * 
	 * @param parent
	 * @return 
	 */
	
	
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		MouseAdapter canvasmouseadapter = new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (!lostgame) 
				{
					int i = e.x / 20;
					int j = e.y / 20;
					
					if (e.button == 1)
					{
						// 左键
						if (IsBomb[i][j])
						{
							lostgame = true;
							for (int a = 0; a < length; a++)
								for (int b = 0; b < length; b++)
									if (IsBomb[a][b])
										btnStatus[a][b] = 1;
						}
						else
						{
							checkcount(i, j);
							int count=0;
							for (int a = 0; a < length; a++)
								for (int b = 0; b < length; b++)
									if (btnStatus[a][b]!=1&&!IsBomb[a][b])count++;
							if(count==0)
							{
					    		lostgame=true;
					    		
								final Shell windialog = new Shell(getShell(), SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
							    windialog.setText("Congratulation!");
							    windialog.setSize(240, 150);
							    windialog.setLocation(402, 319);
								
							    Button winDialogbtnOK = new Button(windialog, SWT.PUSH);
							    winDialogbtnOK.setText("OK");
							    winDialogbtnOK.setBounds(20, 55, 80, 25);
							    winDialogbtnOK.addMouseListener(new MouseAdapter() {
							    	@Override
							    	public void mouseUp(MouseEvent e) {
							    		BombInit();
							    		windialog.dispose();
							    	}
							    });
							    Button winDialogbtnCancel = new Button(windialog, SWT.PUSH);
							    winDialogbtnCancel.setText("Cancel");
							    winDialogbtnCancel.setBounds(120, 55, 80, 25);
							    winDialogbtnCancel.addMouseListener(new MouseAdapter() {
							    	@Override
							    	public void mouseUp(MouseEvent e) {
							    		windialog.dispose();
							    	}
							    });
							    Label winDialogInfo = new Label(windialog, SWT.NONE);
							    winDialogInfo.setText("You Win!Play Again?");
							    winDialogInfo.setBounds(20, 20, 150, 20);
							    windialog.open();
							}
									
						}
					}
					
					if (e.button == 3)
					{
						// 右键
						if(btnStatus[i][j]!=1)
							btnStatus[i][j]=2-btnStatus[i][j];
					}
					
					Board.redraw();
				}
			}

			@Override
			public void mouseUp(MouseEvent e) {

			}

			public void checkcount(int i, int j) {
				btnStatus[i][j] = 1;
				if (bombcount[i][j] == 0) {
					for (int a = -1; a <= 1; a++)
						for (int b = -1; b <= 1; b++) {
							if (i + a >= 0 && i + a < length && j + b >= 0 && j + b < length && !(a == 0 && b == 0))
								if (btnStatus[i + a][j + b] == 0||btnStatus[i + a][j + b] == 2)
									checkcount(i + a, j + b);
						}
				} else
					return;
			}
		};
	
		PaintListener canvaspaintlistener = new PaintListener() {
			@Override
			public void paintControl(PaintEvent arg0) {

				Image BufferImage = new Image(arg0.display,Board.getBounds());
		        GC BufferImageGC = new GC(BufferImage);
		        
				Image DefaultImage = new Image(Display.getCurrent(),".\\pics\\未命名-2.png");
				Image FocusImage = new Image(Display.getCurrent(),".\\pics\\未命名-1.png");
				Image ClickedImage = new Image(Display.getCurrent(),".\\pics\\未命名-3.png");
				Image BombImage = new Image(Display.getCurrent(),".\\pics\\未命名-4.png");
				Image SuspendImage = new Image(Display.getCurrent(),".\\pics\\未命名-5.png");
				
				Font font=new Font(arg0.display, "Arial", 10, 10);
				BufferImageGC.setFont(font);
				
				for(int i=0;i<length;i++)
					for(int j=0;j<length;j++)
					{
						if (btnStatus[i][j] == 1)
						{
							if(IsBomb[i][j])
								BufferImageGC.drawImage(BombImage,i*20, j*20);
							else
							{
								BufferImageGC.drawImage(ClickedImage,i*20, j*20);
								int[] CountColor = {SWT.COLOR_WHITE,SWT.COLOR_BLACK,SWT.COLOR_DARK_RED,SWT.COLOR_DARK_BLUE,SWT.COLOR_DARK_GREEN,SWT.COLOR_DARK_CYAN,SWT.COLOR_DARK_YELLOW,SWT.COLOR_DARK_MAGENTA,SWT.COLOR_DARK_GRAY}; 
								BufferImageGC.setForeground(arg0.display.getSystemColor(CountColor[bombcount[i][j]]));
								if (bombcount[i][j]!=0)BufferImageGC.drawString("" + bombcount[i][j], i*20+3, j*20-2, true);
							}
						}
						else
						{
							if (btninfocus[0] == i && btninfocus[1] == j)
								BufferImageGC.drawImage(FocusImage,i*20, j*20);
							else
								BufferImageGC.drawImage(DefaultImage,i*20, j*20);
							
							if (btnStatus[i][j] == 2)
								BufferImageGC.drawImage(SuspendImage,i*20, j*20);
						}

					}
				
				DefaultImage.dispose();
				FocusImage.dispose();
				ClickedImage.dispose();
				BombImage.dispose();
				SuspendImage.dispose();
				font.dispose();
				
				arg0.gc.drawImage(BufferImage, 0, 0);

				arg0.gc.dispose();				
				BufferImage.dispose();
				BufferImageGC.dispose();
			
			}

		};

		MouseMoveListener canvasmousemovelistener = new MouseMoveListener()
		{
			@Override
			public void mouseMove (MouseEvent e)
			{
				if(!lostgame) {
					int i=e.x/20;
					int j=e.y/20;
					btninfocus[0]=i;btninfocus[1]=j;
					Board.redraw();
				}
			}
		};
		MouseTrackAdapter canvasmousetracklistener = new MouseTrackAdapter() {
			@Override
			public void mouseExit(MouseEvent e) {
				if (!lostgame) {
					btninfocus[0]=-1;btninfocus[1]=-1;
					Board.redraw();
				}
			}
		};

				Board = new Canvas(container, SWT.NO_BACKGROUND);
				Board.addMouseListener(canvasmouseadapter);
				Board.addMouseTrackListener(canvasmousetracklistener);
				Board.addMouseMoveListener(canvasmousemovelistener);
				Board.addPaintListener(canvaspaintlistener);
				Board.setBounds(30, 30, 20*length, 20*length);

		BombInit();

		return container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
		{
			action_restart = new Action("重新开始") {
				public void run() {
					BombInit();
				}
			};
		}
		{
			action_setting = new Action("设置") {
				public void run() {
					
					final Shell paramdialog = new Shell(getShell(), SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
					final Button parambtnSubmit = new Button(paramdialog, SWT.NONE);
					final Button parambtnCancel = new Button(paramdialog, SWT.NONE);
					
					paramdialog.setSize(280, 170);
					paramdialog.setLocation(399, 297);
					paramdialog.setText("设置");
					
					Label paramLabel1 = new Label(paramdialog, SWT.NONE);
					paramLabel1.setBounds(30, 15, 80, 20);
					paramLabel1.setText("大小(5-30)");						
					
					final Text paramlengthinput = new Text(paramdialog, SWT.BORDER);
					paramlengthinput.addFocusListener(new FocusAdapter() {
						@Override
						public void focusLost(FocusEvent e) {
							if (Integer.valueOf(paramlengthinput.getText())>30)paramlengthinput.setText("30");
							if (Integer.valueOf(paramlengthinput.getText())<5)paramlengthinput.setText("5");
						}
					});
					paramlengthinput.addKeyListener(new KeyAdapter() {
						@Override
						public void keyReleased(KeyEvent e) {
							if(e.keyCode==13)
								parambtnSubmit.forceFocus();
						}
					});
					paramlengthinput.setBounds(112, 15, 70, 20);
					paramlengthinput.setText(length+"");
										
					Label paramLabel2 = new Label(paramdialog, SWT.NONE);
					paramLabel2.setBounds(30, 52, 30, 20);
					paramLabel2.setText("难度");
					
					Button[] paramBtndiffinput = new Button[3];
					for(int i=0;i<3;i++) 
					{
						paramBtndiffinput[i]=new Button(paramdialog,SWT.RADIO);
						paramBtndiffinput[i].setBounds(70+60*i, 52, 50, 20);
					}
					paramBtndiffinput[0].setText("简单");
					paramBtndiffinput[1].setText("中等");
					paramBtndiffinput[2].setText("困难");
					if((int)(diff*10)>=1&&(int)(diff*10)<=3)
						paramBtndiffinput[(int)(diff*10)-1].setSelection(true);
					else
						paramBtndiffinput[1].setSelection(true);
					for(int i=0;i<3;i++)
					{
						final int tempi=i+1;
						paramBtndiffinput[i].addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								diff=tempi/10.0;
							}
						});
						paramBtndiffinput[i].addKeyListener(new KeyAdapter() {
							@Override
							public void keyReleased(KeyEvent e) {
								if(e.keyCode==13)
									parambtnSubmit.forceFocus();
							}
						});
					}
					
					parambtnSubmit.setText("确定");
					parambtnSubmit.setBounds(30, 85, 72, 22);
					parambtnSubmit.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							length=Integer.valueOf(paramlengthinput.getText());
							BombInit();
							paramdialog.dispose();
						}
					});
					
					parambtnCancel.setBounds(120, 85, 72, 22);
					parambtnCancel.setText("取消");				
					parambtnCancel.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							paramdialog.dispose();
						}
					});

					paramdialog.open();
				}
			};
		}
	}

	/**
	 * Create the menu manager.
	 * 
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuManager_main = new MenuManager("menu");
		MenuManager menuManager_sub = new MenuManager("选项...");
		menuManager_main.add(menuManager_sub);
		menuManager_sub.add(action_restart);
		menuManager_sub.add(action_setting);
		return menuManager_main;
	}

	/**
	 * Create the toolbar manager.
	 * 
	 * @return the toolbar manager
	 */
	@Override
	protected ToolBarManager createToolBarManager(int style) {
		ToolBarManager toolBarManager = new ToolBarManager(style);
		return toolBarManager;
	}

	/**
	 * Create the status line manager.
	 * 
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			final bomb window = new bomb();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configure the shell.
	 * 
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		Image IconImage = new Image(Display.getCurrent(),".\\pics\\未命名-6.png");
		newShell.setImage(IconImage);
		IconImage.dispose();
		super.configureShell(newShell);
		newShell.setText("Bomb");
	}
}

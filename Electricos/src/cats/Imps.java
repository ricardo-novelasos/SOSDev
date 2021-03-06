//Paquete
package cats;

//Importaciones
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import ptovta.Star;
import ptovta.Login;
import static ptovta.Princip.bIdle;




/*Clase para controlar los impues*/
public class Imps extends javax.swing.JFrame 
{
    /*Contiene el color original del botón*/
    private java.awt.Color      colOri;
    
    /*Declara variables originales globales*/
    private String              sImpueOriG;
    private String              sValOriG;
    private int                 iContCellEd;
    private int                 iContFi;

    /*Almacena el borde original del botón de búscar*/
    private Border              bBordOri;
    
    /*Para controlar si seleccionar todo o deseleccionarlo de la tabla*/
    private boolean             bSel;
    
    
    
    
    /*Constructor sin argumentos*/
    public Imps() 
    {
        /*Inicaliza los componentes gráficos*/
        initComponents();
        
        /*Establece el botón por default*/
        this.getRootPane().setDefaultButton(jBNew);
        
        /*Obtiene el color original que deben tener los botones*/
        colOri  = jBSal.getBackground();
        
        /*Para que no se muevan las columnas*/
        jTab.getTableHeader().setReorderingAllowed(false);
        
        /*Esconde el link de ayuda*/
        jLAyu.setVisible(false);
        
        /*Centra la ventana*/
        this.setLocationRelativeTo(null);
        
        /*Inicialmente esta deseleccionada la tabla*/
        bSel        = false;
        
        /*Establece el titulo de la ventana con El usuario, la fecha y hora*/                
        this.setTitle("Catálogo de impuestos, Usuario: <" + Login.sUsrG + "> " + Login.sFLog);        
        
        /*Inicializa el contador de filas en uno*/
        iContFi          = 1;
        
        /*Para que la tabla este ordenada al mostrarce y al dar clic en el nombre de la columna*/
        TableRowSorter trs = new TableRowSorter<>((DefaultTableModel)jTab.getModel());
        jTab.setRowSorter(trs);
        trs.setSortsOnUpdates(true);
        
        //Establece el ícono de la forma
        Star.vSetIconFram(this);
        
        /*Pon el foco del teclado en el campo de edición de código de impue*/
        jTCod.grabFocus();
        
        /*Establece el tamaño de las columnas de la tabla de impues*/
        jTab.getColumnModel().getColumn(2).setPreferredWidth(200);
        
        /*Activa en la tabla que se usen normamente las teclas de tabulador*/
        jTab.setFocusTraversalKeys(java.awt.KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        jTab.setFocusTraversalKeys(java.awt.KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        
        /*Incializa el contador del cell editor*/
        iContCellEd = 1;
                
        /*Obtén de la base de datos todos los impuesots y cargalos en la tabla*/
        vCargImpues();
            
        /*Crea el listener para la tabla de impues, para cuando se modifique una celda guardarlo en la base de datos el cambio*/
        PropertyChangeListener pro = new PropertyChangeListener() 
        {
            @Override
            public void propertyChange(PropertyChangeEvent event) 
            {                                
                //Si no hay selección entonces regresa                       
                if(jTab.getSelectedRow()==-1)
                    return;
                        
                /*Obtén la propiedad que a sucedio en el control*/
                String pr = event.getPropertyName();                                
                                
                /*Si el evento fue por entrar en una celda de la tabla*/
                if("tableCellEditor".equals(pr)) 
                {
                    /*Si el contador de cell editor está en 1 entonces que lea el valor original que estaba ya*/
                    if(iContCellEd==1)
                    {                                                
                        /*Obtiene algunos datos originales*/
                        sImpueOriG           = jTab.getValueAt(jTab.getSelectedRow(), 1).toString().replace(" ", "").trim();                                                          
                        sValOriG             = jTab.getValueAt(jTab.getSelectedRow(), 2).toString();                                                                                  
                        
                        /*Aumenta el contador del cell*/
                        ++iContCellEd;
                    }
                    /*Else, el contador de cell editor es 2, osea que va de salida*/
                    else
                    {
                        /*Resetea el contador*/
                        iContCellEd             = 1;
                        
                        /*Coloca los valores originales en su lugar*/
                        jTab.setValueAt(sImpueOriG, jTab.getSelectedRow(), 1);
                        
                        /*Obtén el valor del impuesto ingresado*/                                                
                        String sVal             = jTab.getValueAt(jTab.getSelectedRow(), 2).toString();
                                    
                        /*Si los caes introducidos no se puede convertir a double entonces*/
                        try  
                        {  
                            Double.parseDouble(sVal);  
                        }  
                        catch(NumberFormatException expnNumForm)  
                        {          
                            /*Coloca el valor original y regresa*/
                            jTab.setValueAt(sValOriG, jTab.getSelectedRow(), 2);
                            return;
                        }          

                        /*Si el valor del impuesto es menor a 1 etonces*/
                        if(Double.parseDouble(sVal)<=0)
                        {                                                        
                            /*Coloca el valor original y regresa*/
                            jTab.setValueAt(sValOriG, jTab.getSelectedRow(), 2);
                            return;
                        }
                                                              
                        //Abre la base de datos                             
                        Connection  con = Star.conAbrBas(false, true);

                        //Si hubo error entonces regresa
                        if(con==null)
                            return;
        
                        //Declara variables de la base de datos    
                        Statement   st;                                                
                        String      sQ;

                        /*Actualiza en la base de datos el valor del impuesto*/
                        try 
                        {                                                        
                            sQ = "UPDATE impues SET "
                                    + "impueval         = '" + sVal + "', "        
                                    + "fmod             = now(), "
                                    + "estac            = '" + Login.sUsrG.replace("'", "''") + "', "
                                    + "sucu             = '" + Star.sSucu.replace("'", "''") + "', "
                                    + "nocaj            = '" + Star.sNoCaj.replace("'", "''") + "' "
                                    + "WHERE codimpue   = '" + sImpueOriG.replace("'", "''") + "'";                                                
                            st = con.createStatement();
                            st.executeUpdate(sQ);
                         }
                         catch(SQLException expnSQL) 
                         { 
                            //Procesa el error y regresa
                            Star.iErrProc(this.getClass().getName() + " " + expnSQL.getMessage(), Star.sErrSQL, expnSQL.getStackTrace(), con);
                            return;                        
                         }                                                                                     

                        /*Inserta en log de impuesto*/
                        try 
                        {            
                            sQ = "INSERT INTO logimpue(cod,                                         val,        accio,                  estac,                      sucu,                           nocaj,                  falt) " + 
                                          "VALUES('" + sImpueOriG.replace("'", "''") + "', '" +     sVal + "',  'MODIFICAR',      '" +  Login.sUsrG + "','" +    Star.sSucu + "', '" +    Star.sNoCaj + "', now())";                                
                            st = con.createStatement();
                            st.executeUpdate(sQ);
                        }
                        catch(SQLException expnSQL) 
                        { 
                            //Procesa el error y regresa
                            Star.iErrProc(this.getClass().getName() + " " + expnSQL.getMessage(), Star.sErrSQL, expnSQL.getStackTrace(), con);
                            return;                        
                        }

                        //Termina la transacción
                        if(Star.iTermTransCon(con)==-1)
                            return;

                        //Cierra la base de datos
                        Star.iCierrBas(con);                            
                        
                    }/*Fin de else*/                                                                                            
                    
                }/*Fin de if("tableCellEditor".equals(property)) */
                
            }/*Fin de public void propertyChange(PropertyChangeEvent event) */
            
        };
        
        /*Establece el listener para la tabla de impues*/
        jTab.addPropertyChangeListener(pro);
        
    }/*Fin de public Lineas() */


    /*Obtén de la base de datos todos los impuesots y cargalos en la tabla*/
    private void vCargImpues()
    {
        //Abre la base de datos                             
        Connection  con = Star.conAbrBas(true, false);

        //Si hubo error entonces regresa
        if(con==null)
            return;

        /*Crea el modelo para cargar cadenas en el*/
        DefaultTableModel te = (DefaultTableModel)jTab.getModel();                    

        //Declara variables de la base de datos    
        Statement   at;
        ResultSet   rs;        
        String      sQ;

        /*Trae todos los impues de la base de datos y cargalos en la tabla*/
        try
        {
            sQ = "SELECT codimpue, impueval FROM impues WHERE codimpue <> '-'";
            at = con.createStatement();
            rs = at.executeQuery(sQ);
            /*Si hay datos*/
            while(rs.next())
            {
                /*Agregalo a la tabla*/
                Object nu[]     = {iContFi, rs.getString("codimpue"), rs.getString("impueval")};
                te.addRow(nu);
                
                /*Aumenta en uno el contador de filas en uno*/
                ++iContFi;       
            }                        
        }
        catch(SQLException expnSQL)
        {
            //Procesa el error y regresa
            Star.iErrProc(this.getClass().getName() + " " + expnSQL.getMessage(), Star.sErrSQL, expnSQL.getStackTrace(), con);
            return;                        
        }            
        
        //Cierra la base de datos
        Star.iCierrBas(con);
                    
    }/*Fin de private void vCargImpues()*/
            
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jP1 = new javax.swing.JPanel();
        jBDel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jBSal = new javax.swing.JButton();
        jTCod = new javax.swing.JTextField();
        jBNew = new javax.swing.JButton();
        jTVal = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTab = new javax.swing.JTable();
        jBBusc = new javax.swing.JButton();
        jTBusc = new javax.swing.JTextField();
        jBMostT = new javax.swing.JButton();
        jBActua = new javax.swing.JButton();
        jBTab1 = new javax.swing.JButton();
        jLAyu = new javax.swing.JLabel();
        jBTod = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                formMouseWheelMoved(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jP1.setBackground(new java.awt.Color(255, 255, 255));
        jP1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jP1KeyPressed(evt);
            }
        });
        jP1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jBDel.setBackground(new java.awt.Color(255, 255, 255));
        jBDel.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jBDel.setForeground(new java.awt.Color(0, 102, 0));
        jBDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/del5.png"))); // NOI18N
        jBDel.setText("Borrar");
        jBDel.setToolTipText("Borrar Impuesto(s) (Ctrl+SUPR)");
        jBDel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jBDel.setNextFocusableComponent(jBActua);
        jBDel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jBDelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jBDelMouseExited(evt);
            }
        });
        jBDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDelActionPerformed(evt);
            }
        });
        jBDel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jBDelKeyPressed(evt);
            }
        });
        jP1.add(jBDel, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 70, 120, 30));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("*Valor:");
        jP1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 10, 270, -1));

        jBSal.setBackground(new java.awt.Color(255, 255, 255));
        jBSal.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jBSal.setForeground(new java.awt.Color(0, 102, 0));
        jBSal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/sal.png"))); // NOI18N
        jBSal.setText("Salir");
        jBSal.setToolTipText("Salir (ESC)");
        jBSal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jBSal.setNextFocusableComponent(jTCod);
        jBSal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jBSalMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jBSalMouseExited(evt);
            }
        });
        jBSal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSalActionPerformed(evt);
            }
        });
        jBSal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jBSalKeyPressed(evt);
            }
        });
        jP1.add(jBSal, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 130, 120, 30));

        jTCod.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 255), 1, true));
        jTCod.setNextFocusableComponent(jTVal);
        jTCod.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTCodFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTCodFocusLost(evt);
            }
        });
        jTCod.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTCodKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTCodKeyTyped(evt);
            }
        });
        jP1.add(jTCod, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 150, 20));

        jBNew.setBackground(new java.awt.Color(255, 255, 255));
        jBNew.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jBNew.setForeground(new java.awt.Color(0, 102, 0));
        jBNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/agre.png"))); // NOI18N
        jBNew.setText("Nuevo");
        jBNew.setToolTipText("Nuevo Impuesto (Ctrl+N)");
        jBNew.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jBNew.setNextFocusableComponent(jTab);
        jBNew.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jBNewMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jBNewMouseExited(evt);
            }
        });
        jBNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBNewActionPerformed(evt);
            }
        });
        jBNew.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jBNewKeyPressed(evt);
            }
        });
        jP1.add(jBNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 30, 90, 20));

        jTVal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jTVal.setNextFocusableComponent(jBNew);
        jTVal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTValFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTValFocusLost(evt);
            }
        });
        jTVal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTValKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTValKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTValKeyTyped(evt);
            }
        });
        jP1.add(jTVal, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 30, 80, 20));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("Impuestos:");
        jP1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 160, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("*Código Impuesto:");
        jP1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 150, -1));

        jTab.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Código", "Valor Impuesto"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTab.setGridColor(new java.awt.Color(255, 255, 255));
        jTab.setNextFocusableComponent(jBBusc);
        jTab.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jTab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTabKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(jTab);

        jP1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 470, 250));

        jBBusc.setBackground(new java.awt.Color(255, 255, 255));
        jBBusc.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jBBusc.setForeground(new java.awt.Color(0, 102, 0));
        jBBusc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/busc5.png"))); // NOI18N
        jBBusc.setText("Buscar F3");
        jBBusc.setNextFocusableComponent(jTBusc);
        jBBusc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jBBuscMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jBBuscMouseExited(evt);
            }
        });
        jBBusc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBBuscActionPerformed(evt);
            }
        });
        jBBusc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jBBuscKeyPressed(evt);
            }
        });
        jP1.add(jBBusc, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 320, 140, 20));

        jTBusc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        jTBusc.setNextFocusableComponent(jBMostT);
        jTBusc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTBuscFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTBuscFocusLost(evt);
            }
        });
        jTBusc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTBuscKeyPressed(evt);
            }
        });
        jP1.add(jTBusc, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 320, 190, 20));

        jBMostT.setBackground(new java.awt.Color(255, 255, 255));
        jBMostT.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jBMostT.setForeground(new java.awt.Color(0, 102, 0));
        jBMostT.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/mostt.png"))); // NOI18N
        jBMostT.setText("Mostrar F4");
        jBMostT.setToolTipText("Mostrar Nuevamente todos los Registros");
        jBMostT.setNextFocusableComponent(jBDel);
        jBMostT.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jBMostTMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jBMostTMouseExited(evt);
            }
        });
        jBMostT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBMostTActionPerformed(evt);
            }
        });
        jBMostT.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jBMostTKeyPressed(evt);
            }
        });
        jP1.add(jBMostT, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 320, 140, 20));

        jBActua.setBackground(new java.awt.Color(255, 255, 255));
        jBActua.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jBActua.setForeground(new java.awt.Color(0, 102, 0));
        jBActua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/actualizar.png"))); // NOI18N
        jBActua.setText("Actualizar");
        jBActua.setToolTipText("Actualizar Registros (F5)");
        jBActua.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jBActua.setNextFocusableComponent(jBSal);
        jBActua.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jBActuaMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jBActuaMouseExited(evt);
            }
        });
        jBActua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBActuaActionPerformed(evt);
            }
        });
        jBActua.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jBActuaKeyPressed(evt);
            }
        });
        jP1.add(jBActua, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 100, 120, -1));

        jBTab1.setBackground(new java.awt.Color(0, 153, 153));
        jBTab1.setToolTipText("Mostrar Tabla en Grande");
        jBTab1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBTab1ActionPerformed(evt);
            }
        });
        jBTab1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jBTab1KeyPressed(evt);
            }
        });
        jP1.add(jBTab1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 10, 20));

        jLAyu.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLAyu.setForeground(new java.awt.Color(0, 51, 204));
        jLAyu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLAyu.setText("http://Ayuda en Lìnea");
        jLAyu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLAyuMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLAyuMouseExited(evt);
            }
        });
        jP1.add(jLAyu, new org.netbeans.lib.awtextra.AbsoluteConstraints(486, 340, 120, -1));

        jBTod.setBackground(new java.awt.Color(255, 255, 255));
        jBTod.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jBTod.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imgs/marct.png"))); // NOI18N
        jBTod.setText("Marcar todo");
        jBTod.setToolTipText("Marcar Todos los Registros de la Tabla (Alt+T)");
        jBTod.setNextFocusableComponent(jTab);
        jBTod.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jBTodMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jBTodMouseExited(evt);
            }
        });
        jBTod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBTodActionPerformed(evt);
            }
        });
        jBTod.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jBTodKeyPressed(evt);
            }
        });
        jP1.add(jBTod, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 52, 130, 18));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jP1, javax.swing.GroupLayout.DEFAULT_SIZE, 632, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jP1, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    /*Cuando se presiona el botón de borrar*/
    private void jBDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDelActionPerformed
                                               
        /*Si no hay selección en la tabla no puede seguir*/
        if(jTab.getSelectedRow()==-1)
        {
            /*Mensajea*/
            JOptionPane.showMessageDialog(null, "No has seleccionado un impue de la lista para borrar.", "Borrar Impuesto", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource(Star.sRutIconAd)));
            
            /*Coloca el foco del teclado en la tabla y mensajea*/
            jTab.grabFocus();            
            return;            
        }
        
        /*Preguntar al usuario si esta seguro de querer borrar*/
        Object[] op = {"Si","No"};
        int iRes    = JOptionPane.showOptionDialog(this, "¿Seguro que quiere borrar el(los) impue(s)?", "Borrar Impuesto", JOptionPane.YES_NO_OPTION,  JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource(Star.sRutIconDu)), op, op[0]);
        if(iRes==JOptionPane.NO_OPTION || iRes==JOptionPane.CLOSED_OPTION)
            return;
                
        //Abre la base de datos                             
        Connection  con = Star.conAbrBas(false, false);

        //Si hubo error entonces regresa
        if(con==null)
            return;
        
        /*Para saber si hubo cambios*/
        boolean bMov    = false;

        //Declara variables de la base de datos    
        Statement   st;
        ResultSet   rs;        
        String      sQ;
        
        /*Recorre toda la selección del usuario*/
        int iSel[]              = jTab.getSelectedRows();
        DefaultTableModel tm    = (DefaultTableModel)jTab.getModel();
        for(int x = iSel.length - 1; x >= 0; x--)
        {
            /*Comprueba si el impue ya esta utilizado en alguna venta*/        
            try
            {
                sQ = "SELECT codimpue FROM partvta WHERE codimpue = '" + jTab.getValueAt(iSel[x], 1).toString().trim() + "'";                        
                st = con.createStatement();
                rs = st.executeQuery(sQ);
                /*Si hay datos, entonces si existe este impue para alguna venta*/
                if(rs.next())
                {
                    /*Mensajea y continua*/
                    JOptionPane.showMessageDialog(null, "Ya esta este impuesto: " + jTab.getValueAt(iSel[x], 1).toString() + " asignado a algúna venta, no se puede eliminar.", "Impuestos", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource(Star.sRutIconAd)));                   
                    continue;
                }
            }
            catch(SQLException expnSQL)
            {
                //Procesa el error y regresa
                Star.iErrProc(this.getClass().getName() + " " + expnSQL.getMessage(), Star.sErrSQL, expnSQL.getStackTrace(), con);
                return;                        
            }  

            /*Comprueba si el impue ya esta utilizado en algún producto*/        
            try
            {
                sQ = "SELECT impue FROM prods WHERE impue = '" + jTab.getValueAt(iSel[x], 1).toString() + "'";                        
                st = con.createStatement();
                rs = st.executeQuery(sQ);
                /*Si hay datos, entonces si existe este impue para algún producto*/
                if(rs.next())
                {
                    /*Mensajea y continua*/
                    JOptionPane.showMessageDialog(null, "Ya esta este impuesto: " + jTab.getValueAt(iSel[x], 1).toString() + " asignado a algún producto, no se puede eliminar.", "Impuestos", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource(Star.sRutIconAd)));                   
                    continue;
                }
            }
            catch(SQLException expnSQL)
            {
                //Procesa el error y regresa
                Star.iErrProc(this.getClass().getName() + " " + expnSQL.getMessage(), Star.sErrSQL, expnSQL.getStackTrace(), con);
                return;                        
            }  
            
            //Inicia la transacción
            if(Star.iIniTransCon(con)==-1)
                return;

            /*Borra el impuesto*/
            try 
            {                
                sQ = "DELETE FROM impues WHERE codimpue = '" + jTab.getValueAt(iSel[x], 1).toString() + "'";                                           
                st = con.createStatement();
                st.executeUpdate(sQ);
             }
             catch(SQLException expnSQL) 
             { 
                //Procesa el error y regresa
                Star.iErrProc(this.getClass().getName() + " " + expnSQL.getMessage(), Star.sErrSQL, expnSQL.getStackTrace(), con);
                return;                        
             }

            /*Inserta en log de impuestos*/
            try 
            {            
                sQ = "INSERT INTO logimpue(cod,                                                                     val,                                                accio,           estac,                             sucu,                           nocaj,                  falt) " + 
                              "VALUES('" + jTab.getValueAt(iSel[x], 1).toString().replace("'", "''") + "', '" +     jTab.getValueAt(iSel[x], 2).toString() + "',        'BORRAR',        '" + Login.sUsrG + "', '" +    Star.sSucu + "',  '" +    Star.sNoCaj + "', now())";                                
                st = con.createStatement();
                st.executeUpdate(sQ);
            }
            catch(SQLException expnSQL) 
            { 
                //Procesa el error y regresa
                Star.iErrProc(this.getClass().getName() + " " + expnSQL.getMessage(), Star.sErrSQL, expnSQL.getStackTrace(), con);
                return;                        
            }

            //Termina la transacción
            if(Star.iTermTransCon(con)==-1)
                return;

            /*Coloca la bandera para saber que si hubo movimientos*/
            bMov    = true;
            
            /*Borralo de la tabla*/            
            tm.removeRow(iSel[x]);

            /*Resta en uno el contador de filas el contador de filas en uno*/
            --iContFi;
            
        }/*Fin de for(int x = iSel.length - 1; x >= 0; x--)*/                                                                        
         
        //Cierra la base de datos
        if(Star.iCierrBas(con)==-1)
            return;
        
        /*Mensajea de éxito*/
        if(bMov)
            JOptionPane.showMessageDialog(null, "Impuesto(s) borrado(s) con éxito.", "Impuestos", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource(Star.sRutIconAd))); 
        
    }//GEN-LAST:event_jBDelActionPerformed
   
   
    /*Cuando se presiona una tecla en el formulario*/
    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        
        //Llama a la función escalable
        vKeyPreEsc(evt);
        
    }//GEN-LAST:event_formKeyPressed

   
    /*Cuando se presiona una tecla en el botón de borrar*/
    private void jBDelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBDelKeyPressed
        
        //Llama a la función escalable
        vKeyPreEsc(evt);
        
    }//GEN-LAST:event_jBDelKeyPressed

        
    /*Cuando se presiona una tecla en el panel*/
    private void jP1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jP1KeyPressed
        
        //Llama a la función escalable
        vKeyPreEsc(evt);
        
    }//GEN-LAST:event_jP1KeyPressed

    
    /*Cuando se presiona el botón de salir*/
    private void jBSalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSalActionPerformed
        
        /*Pregunta al usuario si esta seguro de salir*/                
        Object[] op = {"Si","No"};
        if((JOptionPane.showOptionDialog(this, "¿Seguro que quieres salir?", "Salir", JOptionPane.YES_NO_OPTION,  JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource(Star.sRutIconDu)), op, op[0])) == JOptionPane.YES_OPTION)
        {
            /*Llama al recolector de basura y cierra la forma*/
            System.gc();                    
            this.dispose();            
        }
        
    }//GEN-LAST:event_jBSalActionPerformed

    
    /*Cuando se presiona una tecla en el botón salir*/
    private void jBSalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBSalKeyPressed
        
        //Llama a la función escalable
        vKeyPreEsc(evt);
        
    }//GEN-LAST:event_jBSalKeyPressed

    
    /*Cuando se presiona una tecla en el botón de agregar*/
    private void jBNewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBNewKeyPressed
        
        //Llama a la función escalable
        vKeyPreEsc(evt);
        
    }//GEN-LAST:event_jBNewKeyPressed

    
    /*Cuando se presiona una tecla en el campo de código de impue*/
    private void jTCodKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTCodKeyPressed

        //Llama a la función escalable
        vKeyPreEsc(evt);
        
    }//GEN-LAST:event_jTCodKeyPressed

    
    /*Cuando se gana el foco del teclado en el campo de código de impue*/
    private void jTCodFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTCodFocusGained
        
        /*Selecciona todo el texto cuando gana el foco*/
        jTCod.setSelectionStart(0);jTCod.setSelectionEnd(jTCod.getText().length());        
        
    }//GEN-LAST:event_jTCodFocusGained

    
    /*Cuando se presiona una tecla en el campo de valor de impue*/
    private void jTValKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTValKeyPressed

        //Llama a la función escalable
        vKeyPreEsc(evt);
        
    }//GEN-LAST:event_jTValKeyPressed

    
    /*Cuando se presiona una  tecla en la tabla de impues*/
    private void jTabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTabKeyPressed
        
        //Llama a la función escalable
        vKeyPreEsc(evt);
        
    }//GEN-LAST:event_jTabKeyPressed

    
    /*Cuando se gana el foco del teclado en el campo de valor de impue*/
    private void jTValFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTValFocusGained
        
        /*Selecciona todo el texto cuando gana el foco*/
        jTVal.setSelectionStart(0);jTVal.setSelectionEnd(jTVal.getText().length());                
        
    }//GEN-LAST:event_jTValFocusGained

    
    /*Cuando se pierde el foco del teclado en el el campo de código de impue*/
    private void jTCodFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTCodFocusLost

        /*Coloca el caret en la posiciòn 0*/
        jTCod.setCaretPosition(0);
        
        /*Coloca el borde negro si tiene datos, caso contrario de rojo*/                               
        if(jTCod.getText().compareTo("")!=0)
            jTCod.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204,204,255)));
                      
    }//GEN-LAST:event_jTCodFocusLost

    
    /*Cuando se pierde el foco del teclado en el campo de valor de impue*/
    private void jTValFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTValFocusLost

        /*Coloca el caret en la posiciòn 0*/
        jTVal.setCaretPosition(0);
        
        /*Coloca el borde negro si tiene datos, caso contrario de rojo*/                               
        if(jTVal.getText().compareTo("")!=0)
            jTVal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204,204,255)));
        
        /*Si el campo excede la cantidad de caes permitidos recortalo*/
        if(jTVal.getText().length()> 20)
            jTVal.setText(jTVal.getText().substring(0, 20));
        
    }//GEN-LAST:event_jTValFocusLost

            
    /*Cuando se libera una tecla en el campo de valor de impue*/
    private void jTValKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTValKeyReleased
        
        //Declara variables locales
        String  sTe;
        
        
        /*Lee el texto introducido por el usuario*/
        sTe = jTVal.getText();
        
        /*Conviertelo a mayúsculas*/
        sTe = sTe.toUpperCase();
        
        /*Vuelve a colocarlo*/
        jTVal.setText(sTe);
        
    }//GEN-LAST:event_jTValKeyReleased

    
    /*Cuando se presiona el botón de agregar*/
    private void jBNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNewActionPerformed
                                      
        /*Si hay cadena vacia en el campo del código del impue no puede continuar*/
        if(jTCod.getText().replace(" ", "").trim().compareTo("")==0)
        {
            /*Coloca el borde rojo*/                               
            jTCod.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.RED));

            /*Mensajea*/
            JOptionPane.showMessageDialog(null, "El código del impuesto esta vacio.", "Campo vacio", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource(Star.sRutIconAd)));
            
            /*Pon el foco del teclado en el campo de edición y regresa*/
            jTCod.grabFocus();                        
            return;            
        }
        
        /*Si hay cadena vacia en el campo de valor de impue no puede continuar*/
        if(jTVal.getText().compareTo("")==0)
        {
            /*Coloca el borde rojo*/                               
            jTVal.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.RED));
            
            /*Mensajea*/
            JOptionPane.showMessageDialog(null, "El campo de valor de impuesto esta vacio.", "Campo vacio", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource(Star.sRutIconAd)));
            
            /*Pon el foco del teclado en el campo de edición y regresa*/
            jTVal.grabFocus();            
            return;            
        }else
        {
            int contador=0;
            String test=jTVal.getText();
            for(int z=0;z<test.length();z++)
            {
                if(test.charAt(z)=='.')
                    contador++;
            }
            if(contador>1)
            {
            /*Coloca el borde rojo*/                               
            jTVal.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.RED));
            
            /*Mensajea*/
            JOptionPane.showMessageDialog(null, "El campo de valor de impuesto no puede tener mas de un punto decimal.", "Decimal", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource(Star.sRutIconAd)));
            
            /*Pon el foco del teclado en el campo de edición y regresa*/
            jTVal.grabFocus();            
            return;
            }   
        }
        /*Pregunta al usuario si están bien los datos*/                
        Object[] op = {"Si","No"};
        int iRes    = JOptionPane.showOptionDialog(this, "¿Seguro que están bien los datos?", "Agregar impuesto", JOptionPane.YES_NO_OPTION,  JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource(Star.sRutIconDu)), op, op[0]);
        if(iRes==JOptionPane.NO_OPTION || iRes==JOptionPane.CLOSED_OPTION)
            return;
                
        //Abre la base de datos                             
        Connection  con = Star.conAbrBas(false, false);

        //Si hubo error entonces regresa
        if(con==null)
            return;
        
        //Comprueba si el impuesto ya existe en la base de datos
        iRes        = Star.iExiste(con, jTCod.getText().replace(" ", "").trim(), "impues", "codimpue");
        
        //Si hubo error entonces regresa
        if(iRes==-1)
            return;
        
        //Si existe el impuesto entonces
        if(iRes==1)
        {
            //Cierra la base de datos
            if(Star.iCierrBas(con)==-1)
                return;
            
            /*Coloca el borde rojo*/                               
            jTCod.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.RED));

             /*Avisa al usuario*/
            JOptionPane.showMessageDialog(null, "El código del impueto: " + jTCod.getText().replace(" ", "").trim() + " ya existe.", "Registro existente", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource(Star.sRutIconAd))); 

            /*Pon el foco del teclado en el campo del código del impue y regresa*/
            jTCod.grabFocus();               
            return;                
        }
        
        //Inicia la transacción
        if(Star.iIniTransCon(con)==-1)
            return;
        
        //Declara variables de la base de datos    
        Statement   st;                
        String      sQ;
        
        /*Guarda los datos en la base de datos*/
        try 
        {            
            sQ = "INSERT INTO impues(codimpue,                                                                                          impueval,                   falt,       estac,                                          sucu,                                           nocaj) " + 
                        "VALUES('" + jTCod.getText().replace(" ", "").trim().replace("'", "''").replace(",", "") + "','" +              jTVal.getText() + "',       now(), '" + Login.sUsrG.replace("'", "''") + "','" +    Star.sSucu.replace("'", "''") + "','" +   Star.sNoCaj.replace("'", "''") + "')";                                
            st = con.createStatement();
            st.executeUpdate(sQ);
         }
         catch(SQLException expnSQL) 
         { 
            //Procesa el error y regresa
            Star.iErrProc(this.getClass().getName() + " " + expnSQL.getMessage(), Star.sErrSQL, expnSQL.getStackTrace(), con);
            return;                        
         }
        
        /*Inserta en log de impues*/
        try 
        {            
            sQ = "INSERT INTO logimpue(cod,                                                                         val,                    accio,                  estac,                          sucu,                           nocaj,                  falt) " + 
                          "VALUES('" + jTCod.getText().replace(" ", "").trim().replace("'", "''") + "', '" +        jTVal.getText() + "',   'AGREGAR',       '" +   Login.sUsrG + "',   '" +    Star.sSucu + "',  '" +    Star.sNoCaj + "', now())";                                
            st = con.createStatement();
            st.executeUpdate(sQ);
        }
        catch(SQLException expnSQL) 
        { 
            //Procesa el error y regresa
            Star.iErrProc(this.getClass().getName() + " " + expnSQL.getMessage(), Star.sErrSQL, expnSQL.getStackTrace(), con);
            return;                        
        }

        //Termina la transacción
        if(Star.iTermTransCon(con)==-1)
            return;
        
        //Cierra la base de datos
        if(Star.iCierrBas(con)==-1)
            return;

        /*Agrega el registro del impue en la tabla*/
        DefaultTableModel te    = (DefaultTableModel)jTab.getModel();
        Object nu[]             = {iContFi, jTCod.getText().replace(" ", "").trim(), jTVal.getText()};
        te.addRow(nu);
        
        /*Aumenta en uno el contador de filas en uno*/
        ++iContFi;
        
        /*Pon el foco del teclado en el campo del código del impue*/
        jTCod.grabFocus();
        
        /*Limpia los campos*/
        jTCod.setText   ("");
        jTVal.setText   ("");
        
        /*Mensajea de éxito*/
        JOptionPane.showMessageDialog(null, "Impuesto: " + jTCod.getText().replace(" ", "").trim() + " agregado con éxito.", "Exito al agregar", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource(Star.sRutIconAd)));                    
         
    }//GEN-LAST:event_jBNewActionPerformed

    
    /*Cuando se tipea una tecla en el campo de valor*/
    private void jTValKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTValKeyTyped
        
        /*Si el campo excede la cantidad de caes permitidos recortalo*/
        if(jTVal.getText().length()> 30)
            jTVal.setText(jTVal.getText().substring(0, 30));
        
        /*Comprueba que el carácter este en los límites permitidos para numeración*/
        if(((evt.getKeyChar() < '0') || (evt.getKeyChar() > '9')) && (evt.getKeyChar() != '\b') && (evt.getKeyChar() != '.'))        
            evt.consume();        
        
    }//GEN-LAST:event_jTValKeyTyped

    
    /*Cuando se tipea una tecla en el campo de código del impue*/
    private void jTCodKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTCodKeyTyped
        
        /*Si el carácter introducido es minúscula entonces colocalo en el campo con mayúsculas*/
        if(Character.isLowerCase(evt.getKeyChar()))       
            evt.setKeyChar(Character.toUpperCase(evt.getKeyChar()));                              
        
    }//GEN-LAST:event_jTCodKeyTyped

    
    /*Cuando se presiona una tecla en el botón de buscar*/
    private void jBBuscKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBBuscKeyPressed

        //Llama a la función escalable
        vKeyPreEsc(evt);
        
    }//GEN-LAST:event_jBBuscKeyPressed

    
    /*Cuando se gana el foco del teclado en el campo de buscar*/
    private void jTBuscFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTBuscFocusGained

        /*Establece el botón por default*/
        this.getRootPane().setDefaultButton(jBBusc);
        
        /*Selecciona todo el texto cuando gana el foco*/
        jTBusc.setSelectionStart(0);jTBusc.setSelectionEnd(jTBusc.getText().length());
        
    }//GEN-LAST:event_jTBuscFocusGained

    
    /*Cuando se presiona una tecla en el campo de buscar*/
    private void jTBuscKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTBuscKeyPressed

        /*Si se presionó enter entonces presiona el botón de búsqueda y regresa*/
        if(evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jBBusc.doClick();
            return;
        }
        
        //Llama a la función escalable
        vKeyPreEsc(evt);
        
    }//GEN-LAST:event_jTBuscKeyPressed

    
    /*Cuando se presiona una tecla en el botón de mostrar todo*/
    private void jBMostTKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBMostTKeyPressed

        //Llama a la función escalable
        vKeyPreEsc(evt);
        
    }//GEN-LAST:event_jBMostTKeyPressed


    /*Función para mostrar los elementos actualizados en la tabla*/
    private void vCargT()
    {
        /*Borra todos los item en la tabla*/
        DefaultTableModel dm = (DefaultTableModel)jTab.getModel();
        dm.setRowCount(0);
        
        /*Resetea el contador de filas*/
        iContFi = 1;
        
        /*Obtén de la base de datos todos los impuesots y cargalos en la tabla*/
        vCargImpues();
       
        /*Vuelve a poner el foco del teclaod en el campo de buscar*/
        jTBusc.grabFocus();
    }
    
    
    /*Cuando se presiona el botón de mostrar todo*/
    private void jBMostTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBMostTActionPerformed
        
        /*Función para mostrar los elementos actualizados en la tabla*/
        vCargT();
            
    }//GEN-LAST:event_jBMostTActionPerformed

    
    /*Cuando se presiona el botón buscar*/
    private void jBBuscActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBuscActionPerformed
        
        /*Si el campo de buscar esta vacio no puede seguir*/
        if(jTBusc.getText().compareTo("")==0)
        {
            /*Coloca el borde rojo*/                               
            jTBusc.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.RED));
            
            /*Mensajea*/
            JOptionPane.showMessageDialog(null, "El campo de búsqueda esta vacio.", "Campo vacio", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource(Star.sRutIconAd)));

            /*Coloca el foco del teclado en el campo de búsqueda y regresa*/
            jTBusc.grabFocus();           
            return;
        }                      
                
        //Abre la base de datos                             
        Connection  con = Star.conAbrBas(true, false);

        //Si hubo error entonces regresa
        if(con==null)
            return;
        
        /*Borra todos los item en la tabla de impues*/
        DefaultTableModel dm = (DefaultTableModel)jTab.getModel();
        dm.setRowCount(0);
        
         /*Resetea el contador de filas*/
        iContFi                 = 1;

        //Declara variables de la base de datos
        Statement   at;
        ResultSet   rs;        
        String      sQ; 
        
        /*Obtiene de la base de datos todos los impues*/        
        try
        {                  
            sQ = "SELECT * FROM impues WHERE codimpue LIKE('%" + jTBusc.getText().replace(" ", "%") + "%') OR impueval LIKE('%" + jTBusc.getText().replace(" ", "%") + "%') OR falt LIKE('%" + jTBusc.getText().replace(" ", "%") + "%') OR fmod LIKE('%" + jTBusc.getText().replace(" ", "%") + "%')";
            at = con.createStatement();
            rs = at.executeQuery(sQ);
            /*Si hay datos*/
            while(rs.next())
            {
                /*Agregalo a la tabla*/
                DefaultTableModel te= (DefaultTableModel)jTab.getModel();
                Object nu[]         = {iContFi, rs.getString("codimpue"), rs.getString("impueval")};
                te.addRow(nu);

                /*Aumenta en uno el contador de filas*/
                ++iContFi;                                       
            }                        
        }
        catch(SQLException expnSQL)
        {
            //Procesa el error y regresa
            Star.iErrProc(this.getClass().getName() + " " + expnSQL.getMessage(), Star.sErrSQL, expnSQL.getStackTrace(), con);
            return;                        
        }
        
        //Cierra la base de datos
        if(Star.iCierrBas(con)==-1)
            return;
            
        /*Vuelve a poner el foco del teclaod en el campo de buscar*/
        jTBusc.grabFocus();
        
    }//GEN-LAST:event_jBBuscActionPerformed

    
    /*Cuando se presiona el botón de actualizar*/
    private void jBActuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBActuaActionPerformed

        /*Función para cargar todos los elementos en la tabla*/
        vCargT();

    }//GEN-LAST:event_jBActuaActionPerformed

    
    /*Cuando se presiona una tecla en el botón de actualizar*/
    private void jBActuaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBActuaKeyPressed

        //Llama a la función escalable
        vKeyPreEsc(evt);

    }//GEN-LAST:event_jBActuaKeyPressed

    
    /*Cuando se mueve la rueda del ratón en la forma*/
    private void formMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_formMouseWheelMoved
        
        /*Pon la bandera para saber que ya hubó un evento y no se desloguie*/
        bIdle   = true;
        
    }//GEN-LAST:event_formMouseWheelMoved

    
    /*Cuando se arrastra el mouse en la forma*/
    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        
        /*Pon la bandera para saber que ya hubó un evento y no se desloguie*/
        bIdle   = true;
        
    }//GEN-LAST:event_formMouseDragged

    
    /*Cuando se mueve el mouse en la forma*/
    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        
        /*Pon la bandera para saber que ya hubó un evento y no se desloguie*/
        bIdle   = true;
        
    }//GEN-LAST:event_formMouseMoved

            
    /*Cuando se presiona el botón de ver tabla*/
    private void jBTab1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBTab1ActionPerformed

        //Muestra la tabla maximizada
        Star.vMaxTab(jTab);       

    }//GEN-LAST:event_jBTab1ActionPerformed

    
    /*Cuando se presiona una tecla en el botón de ver tabla*/
    private void jBTab1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBTab1KeyPressed

        //Llama a la función escalable
        vKeyPreEsc(evt);

    }//GEN-LAST:event_jBTab1KeyPressed

    
    /*Cuando el mouse entra en el botón de bùscar*/
    private void jBBuscMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBBuscMouseEntered
        
        /*Cambia el color del fondo del botón*/
        jBBusc.setBackground(Star.colBot);
        
        /*Guardar el borde original que tiene el botón*/
        bBordOri    = jBBusc.getBorder();
                
        /*Aumenta el grosor del botón*/
        jBBusc.setBorder(new LineBorder(Color.GREEN, 3));
        
    }//GEN-LAST:event_jBBuscMouseEntered

    
    /*Cuando el mouse sale del botón de búscar*/
    private void jBBuscMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBBuscMouseExited
        
        /*Cambia el color del fondo del botón al original*/
        jBNew.setBackground(colOri);
        
        /*Coloca el borde que tenía*/
        jBBusc.setBorder(bBordOri);
        
    }//GEN-LAST:event_jBBuscMouseExited

    
    /*Cuando el mouse entra en el campo del link de ayuda*/
    private void jLAyuMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLAyuMouseEntered
        
        /*Cambia el cursor del ratón*/
        this.setCursor( new Cursor(Cursor.HAND_CURSOR));
        
    }//GEN-LAST:event_jLAyuMouseEntered

    
    /*Cuando el mouse sale del campo del link de ayuda*/
    private void jLAyuMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLAyuMouseExited
        
        /*Cambia el cursor del ratón al que tenía*/
        this.setCursor( new Cursor(Cursor.DEFAULT_CURSOR));	
        
    }//GEN-LAST:event_jLAyuMouseExited

    
    /*Cuando se presiona el botón de seleccionar todo*/
    private void jBTodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBTodActionPerformed

        /*Si la tabla no contiene elementos entonces regresa*/
        if(jTab.getRowCount()==0)
            return;
        
        /*Si están seleccionados los elementos en la tabla entonces*/
        if(bSel)
        {
            /*Coloca la bandera y deseleccionalos*/
            bSel    = false;
            jTab.clearSelection();
        }
        /*Else deseleccionalos y coloca la bandera*/
        else
        {
            bSel    = true;
            jTab.setRowSelectionInterval(0, jTab.getModel().getRowCount()-1);
        }

    }//GEN-LAST:event_jBTodActionPerformed

    
    /*Cuando se presiona una tecla en el botón de seleccionar todo*/
    private void jBTodKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBTodKeyPressed

        //Llama a la función escalable
        vKeyPreEsc(evt);

    }//GEN-LAST:event_jBTodKeyPressed

    
    /*Cuando se esta saliendo de la forma*/
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        
        /*Presiona el botón de salir*/
        jBSal.doClick();
        
    }//GEN-LAST:event_formWindowClosing

    
    /*Cuando el mouse entra en el botón específico*/
    private void jBNewMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBNewMouseEntered
        
        /*Cambia el color del fondo del botón*/
        jBNew.setBackground(Star.colBot);
        
    }//GEN-LAST:event_jBNewMouseEntered

    
    /*Cuando el mouse entra en el botón específico*/
    private void jBTodMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBTodMouseEntered
        
        /*Cambia el color del fondo del botón*/
        jBTod.setBackground(Star.colBot);
        
    }//GEN-LAST:event_jBTodMouseEntered

    
    /*Cuando el mouse entra en el botón específico*/
    private void jBDelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBDelMouseEntered
        
        /*Cambia el color del fondo del botón*/
        jBDel.setBackground(Star.colBot);
        
    }//GEN-LAST:event_jBDelMouseEntered

    
    /*Cuando el mouse entra en el botón específico*/
    private void jBActuaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBActuaMouseEntered
        
        /*Cambia el color del fondo del botón*/
        jBActua.setBackground(Star.colBot);
        
    }//GEN-LAST:event_jBActuaMouseEntered


    
    /*Cuando el mouse entra en el botón específico*/
    private void jBSalMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBSalMouseEntered
        
        /*Cambia el color del fondo del botón*/
        jBSal.setBackground(Star.colBot);
        
    }//GEN-LAST:event_jBSalMouseEntered

    
    /*Cuando el mouse entra en el botón específico*/
    private void jBMostTMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBMostTMouseEntered
        
        /*Cambia el color del fondo del botón*/
        jBMostT.setBackground(Star.colBot);
        
    }//GEN-LAST:event_jBMostTMouseEntered

    
    /*Cuando el mouse sale del botón específico*/
    private void jBNewMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBNewMouseExited
        
        /*Cambia el color del fondo del botón al original*/
        jBNew.setBackground(colOri);
        
    }//GEN-LAST:event_jBNewMouseExited

    
    /*Cuando el mouse sale del botón específico*/
    private void jBTodMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBTodMouseExited
        
        /*Cambia el color del fondo del botón al original*/
        jBTod.setBackground(colOri);
        
    }//GEN-LAST:event_jBTodMouseExited

    
    /*Cuando el mouse sale del botón específico*/
    private void jBDelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBDelMouseExited
        
        /*Cambia el color del fondo del botón al original*/
        jBDel.setBackground(colOri);
        
    }//GEN-LAST:event_jBDelMouseExited

    
    /*Cuando el mouse sale del botón específico*/
    private void jBActuaMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBActuaMouseExited
        
        /*Cambia el color del fondo del botón al original*/
        jBActua.setBackground(colOri);
        
    }//GEN-LAST:event_jBActuaMouseExited

    
    
    /*Cuando el mouse sale del botón específico*/
    private void jBSalMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBSalMouseExited
        
        /*Cambia el color del fondo del botón al original*/
        jBSal.setBackground(colOri);
        
    }//GEN-LAST:event_jBSalMouseExited

    
    /*Cuando el mouse sale del botón específico*/
    private void jBMostTMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBMostTMouseExited
        
        /*Cambia el color del fondo del botón al original*/
        jBMostT.setBackground(colOri);
        
    }//GEN-LAST:event_jBMostTMouseExited

    
    /*Cuando se pierde el foco del teclado en el control de bùsqueda*/
    private void jTBuscFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTBuscFocusLost

        /*Establece el botón por default*/
        this.getRootPane().setDefaultButton(jBNew);
        
        /*Coloca el caret en la posiciòn 0*/
        jTBusc.setCaretPosition(0);
        
        /*Coloca el borde negro si tiene datos, caso contrario de rojo*/                               
        if(jTBusc.getText().compareTo("")!=0)
            jTBusc.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(204,204,255)));
        
    }//GEN-LAST:event_jTBuscFocusLost
    
    
    /*Función escalable para cuando se presiona una tecla en el módulo*/
    void vKeyPreEsc(java.awt.event.KeyEvent evt)
    {
        /*Pon la bandera para saber que ya hubó un evento y no se desloguie*/
        bIdle   = true;
        
        /*Si se presiona la tecla de escape presiona el botón de salir*/
        if(evt.getKeyCode() == KeyEvent.VK_ESCAPE) 
            jBSal.doClick();
        /*Else if se presiona Alt + T entonces presiona el botón de marcar todo*/
        else if(evt.isAltDown() && evt.getKeyCode() == KeyEvent.VK_T)
            jBTod.doClick();
        /*Si se presiona CTRL + SUP entonces presiona el botón de borrar*/
        else if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_DELETE)
            jBDel.doClick();
        /*Si se presiona CTRL + N entonces presiona el botón de nuevo*/
        else if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_N)
            jBNew.doClick();
        /*Si se presiona F3 presiona el botón de bùscar*/
        else if(evt.getKeyCode() == KeyEvent.VK_F3)
            jBBusc.doClick();
        /*Else if se presiona Alt + F4 entonces presiona el botón de salir*/
        else if(evt.isAltDown() && evt.getKeyCode() == KeyEvent.VK_F4)
            jBSal.doClick();
        /*Si se presiona F4 presiona el botón de mostrar todo*/
        else if(evt.getKeyCode() == KeyEvent.VK_F4)
            jBMostT.doClick();
        /*Si se presiona F5 presiona el botón de actualizar*/
        else if(evt.getKeyCode() == KeyEvent.VK_F5)
            jBActua.doClick();
        
    }/*Fin de void vKeyPreEsc(java.awt.event.KeyEvent evt)*/
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBActua;
    private javax.swing.JButton jBBusc;
    private javax.swing.JButton jBDel;
    private javax.swing.JButton jBMostT;
    private javax.swing.JButton jBNew;
    private javax.swing.JButton jBSal;
    private javax.swing.JButton jBTab1;
    private javax.swing.JButton jBTod;
    private javax.swing.JLabel jLAyu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jP1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTBusc;
    private javax.swing.JTextField jTCod;
    private javax.swing.JTextField jTVal;
    private javax.swing.JTable jTab;
    // End of variables declaration//GEN-END:variables

}/*Fin de public class Clientes extends javax.swing.JFrame */

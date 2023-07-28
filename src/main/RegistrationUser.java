package main;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import config.DBConnectionMgr;
import event.AddUserButtonMouseListener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class RegistrationUser extends JFrame {

	private JPanel contentPane;
	private JTextField usernameTextField;
	private JTextField passwordTextField;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RegistrationUser frame = new RegistrationUser();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public RegistrationUser() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		usernameTextField = new JTextField();
		usernameTextField.setBounds(29, 36, 159, 21);
		contentPane.add(usernameTextField);
		usernameTextField.setColumns(10);
		
		passwordTextField = new JTextField();
		passwordTextField.setBounds(241, 36, 153, 21);
		contentPane.add(passwordTextField);
		passwordTextField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("아이디");
		lblNewLabel.setBounds(29, 10, 57, 15);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("비밀번호");
		lblNewLabel_1.setBounds(241, 11, 57, 15);
		contentPane.add(lblNewLabel_1);
		
		
		
		JButton addUserButton = new JButton("추가");
//		MouseListener addUserButtonMouseListener = new AddUserButtonMouseListener();
//		//mouseclicked 이벤트 메소드가 오버라이딩 된 클래스 객체로 생성(업캐스팅)
//		addUserButton.addMouseListener(addUserButtonMouseListener);
		
		//위와 같음
		addUserButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				if(!insertUser(usernameTextField.getText(), passwordTextField.getText())) {
					JOptionPane.showMessageDialog(contentPane, "사용자 추가 실패!", "insert 오류", JOptionPane.ERROR_MESSAGE);
					return;
				}
				updateUserListTable(table);
			}
		});
		addUserButton.setBounds(29, 67, 365, 36);
		contentPane.add(addUserButton);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(29, 124, 365, 105);
		contentPane.add(scrollPane);
		
		table = new JTable();
		table.setModel(getUserTableModel()); //DefaultTableModel
		scrollPane.setViewportView(table);
		
	}
	
	private boolean insertUser(String username, String password) {
		DBConnectionMgr pool = DBConnectionMgr.getInstance();
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean result = false;
		
		try {
			con = pool.getConnection();
			String sql = "insert into user_tb values(0, ?, ?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			
			result = pstmt.executeUpdate() != 0;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		
		return result;
	}
	
	private void updateUserListTable(JTable jTable) {
		jTable.setModel(getUserTableModel());
	}
	
	public DefaultTableModel getUserTableModel() {
		String[] header = new String[] { "user_id", "username", "password" };
		List<List<Object>> userListAll = getUserListAll();
		
		Object[][] userModelArray = new Object[userListAll.size()][userListAll.get(0).size()]; //2차원 리스트
		
		for(int i = 0; i < userListAll.size(); i++) {
			for(int j =0; j < userListAll.get(i).size(); j++) { //get(i).size(): 내부 리스트의 size()
				userModelArray[i][j] = userListAll.get(i).get(j);
			}
		}
		
		return new DefaultTableModel(userModelArray, header); //원래 table.setModel(new DefaultTableModel( 대충 Object[][]타입, String 타입 ));
	}
	
	public List<List<Object>> getUserListAll() {
		
		DBConnectionMgr pool = DBConnectionMgr.getInstance();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
	  
		List<List<Object>> mstList = new ArrayList<>();
		
		try {
	         con = pool.getConnection();
	         
	         String sql = "select * from user_tb";
	         pstmt = con.prepareStatement(sql); 
	         
	         rs = pstmt.executeQuery();
	         
	         while(rs.next()) {
	        	 ArrayList<Object> dtlList = new ArrayList<>();
	        	 dtlList.add(rs.getInt(1));
	        	 dtlList.add(rs.getString(2));
	        	 dtlList.add(rs.getString(3));
	        	 mstList.add(dtlList);
	         }
	         
	      } catch (Exception e) {
	         e.printStackTrace();
	      } finally {
	         pool.freeConnection(con, pstmt, rs);
	      }
		
		return mstList;
	}
	
}

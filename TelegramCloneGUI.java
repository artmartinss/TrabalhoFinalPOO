import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TelegramCloneGUI extends JFrame {

    private JTree arvoreContatos;
    private JTextPane areaChat;
    private JTextField campoMensagem;
    private JButton botaoEnviar;
    private JLabel labelChatAtual;
    private String chatAtual = "Ninguém";

    public TelegramCloneGUI() {
        setTitle("Telegram Clone");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setJMenuBar(criarMenu());
        add(criarPainelContatos(), BorderLayout.WEST);
        add(criarPainelChat(), BorderLayout.CENTER);
        adicionarListeners();
    }

    private JMenuBar criarMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuArquivo = new JMenu("Arquivo");
        JMenu menuAjuda = new JMenu("Ajuda");

        JMenuItem itemSalvarChat = new JMenuItem("Salvar Chat...");
        JMenuItem itemLimparChat = new JMenuItem("Limpar Chat Atual");
        JMenuItem itemSair = new JMenuItem("Sair");
        JMenuItem itemSobre = new JMenuItem("Sobre");

        itemSalvarChat.addActionListener(e -> executarTarefaSalvarChat());
        itemLimparChat.addActionListener(e -> executarTarefaLimparChat());
        itemSair.addActionListener(e -> executarTarefaSair());
        
        itemSobre.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Telegram Clone\n\nIntegrantes do Grupo:\n1. Arthur Magagnin", 
                "Sobre", 
                JOptionPane.INFORMATION_MESSAGE));

        menuArquivo.add(itemSalvarChat);
        menuArquivo.add(itemLimparChat);
        menuArquivo.addSeparator();
        menuArquivo.add(itemSair);

        menuAjuda.add(itemSobre);

        menuBar.add(menuArquivo);
        menuBar.add(menuAjuda);
        return menuBar;
    }

    private JScrollPane criarPainelContatos() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Telegram");
        DefaultMutableTreeNode grupos = new DefaultMutableTreeNode("Grupos");
        DefaultMutableTreeNode contatos = new DefaultMutableTreeNode("Contatos Diretos");

        grupos.add(new DefaultMutableTreeNode("Grupo da Família"));
        grupos.add(new DefaultMutableTreeNode("Grupo do Trabalho"));
        grupos.add(new DefaultMutableTreeNode("Programação II"));

        contatos.add(new DefaultMutableTreeNode("Ana"));
        contatos.add(new DefaultMutableTreeNode("Bruno"));
        contatos.add(new DefaultMutableTreeNode("Professor"));

        root.add(grupos);
        root.add(contatos);

        arvoreContatos = new JTree(new DefaultTreeModel(root));
        arvoreContatos.setRootVisible(false);

        JScrollPane scrollPane = new JScrollPane(arvoreContatos);
        scrollPane.setPreferredSize(new Dimension(200, 0));
        return scrollPane;
    }

    private JPanel criarPainelChat() {
        JPanel painelChat = new JPanel(new BorderLayout(5, 5));
        painelChat.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        labelChatAtual = new JLabel("Selecione um contato ou grupo para começar");
        labelChatAtual.setFont(new Font("Arial", Font.BOLD, 14));
        painelChat.add(labelChatAtual, BorderLayout.NORTH);

        areaChat = new JTextPane(); 
        areaChat.setEditable(false);
        areaChat.setFont(new Font("Arial", Font.PLAIN, 12));
        areaChat.setText("Bem-vindo!\n");
        painelChat.add(new JScrollPane(areaChat), BorderLayout.CENTER);

        JPanel painelInput = new JPanel(new BorderLayout(5, 0));
        campoMensagem = new JTextField("Digite sua mensagem...");
        botaoEnviar = new JButton("Enviar");
        
        painelInput.add(campoMensagem, BorderLayout.CENTER);
        painelInput.add(botaoEnviar, BorderLayout.EAST);

        painelChat.add(painelInput, BorderLayout.SOUTH);
        return painelChat;
    }

    private void adicionarListeners() {
        arvoreContatos.getSelectionModel().addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) arvoreContatos.getLastSelectedPathComponent();
            if (node != null && node.isLeaf()) {
                chatAtual = node.getUserObject().toString();
                executarTarefaSelecionarChat();
            }
        });

        botaoEnviar.addActionListener(e -> executarTarefaEnviarMensagem());
        campoMensagem.addActionListener(e -> executarTarefaEnviarMensagem());
    }

    private void executarTarefaSelecionarChat() {
        labelChatAtual.setText("Conversa com: " + chatAtual);
        areaChat.setText("Você abriu o chat com " + chatAtual + ".\n");
        areaChat.setForeground(Color.BLUE); 
        campoMensagem.requestFocus(); 
    }

    private void executarTarefaEnviarMensagem() {
        String mensagem = campoMensagem.getText().trim();
        if (mensagem.isEmpty() || chatAtual.equals("Ninguém")) {
            if (chatAtual.equals("Ninguém")) {
                JOptionPane.showMessageDialog(this, "Selecione um chat primeiro!", "Erro", JOptionPane.WARNING_MESSAGE);
            }
            return; 
        }

        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String mensagemFormatada = String.format("[%s] Eu: %s\n", timestamp, mensagem);

        try {
            areaChat.setForeground(Color.BLACK);
            areaChat.getDocument().insertString(areaChat.getDocument().getLength(), mensagemFormatada, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        campoMensagem.setText(""); 
    }

    private void executarTarefaSalvarChat() {
        if (chatAtual.equals("Ninguém")) {
            JOptionPane.showMessageDialog(this, "Nenhum chat selecionado para salvar.", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nomeArquivo = "chat_" + chatAtual.replaceAll("[^a-zA-Z0-9]", "_") + ".txt";

        try (PrintWriter out = new PrintWriter(new FileWriter(nomeArquivo))) {
            out.println("--- Histórico do Chat com: " + chatAtual + " ---");
            out.println(areaChat.getText());
            
            JOptionPane.showMessageDialog(this, "Chat salvo com sucesso em:\n" + nomeArquivo, "Persistência OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar o arquivo: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executarTarefaLimparChat() {
        if (!chatAtual.equals("Ninguém")) {
            areaChat.setText("Chat com " + chatAtual + " foi limpo.\n");
        } else {
            areaChat.setText("Chat limpo.\n");
        }
    }

    private void executarTarefaSair() {
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            new TelegramCloneGUI().setVisible(true);
        });
    }
}
import database.ConexaoPostgres;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;

public class MainApp extends JFrame {

    private JTextField txtNomeCliente, txtEmailCliente, txtTelefoneCliente;
    private JComboBox<ItemCombo> cbClientes, cbProdutos;
    private JTextField txtQuantidade, txtValorBruto, txtValorDesconto, txtValorFinal;
    private JButton btnCalcularDesconto, btnCriarPedido;
    private JTable tabelaView;
    private DefaultTableModel modeloTabela;
    private JTextField txtPedidoIdProcedure;
    private JButton btnExecutarProcedure, btnRecarregarView;

    public MainApp() {
        setTitle("Sistema de Gestão de Vendas - PostgreSQL (pgAdmin)");
        setSize(850, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane abas = new JTabbedPane();

        abas.addTab("1. Cadastrar Cliente", criarPainelClientes());
        abas.addTab("2. Novo Pedido (Function)", criarPainelNovoPedido());
        abas.addTab("3. Relatório de Pedidos (View & Procedure)", criarPainelDashboard());

        add(abas);

        recarregarCombos();
        carregarDadosView();
    }

    private JPanel criarPainelClientes() {
        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNomeCliente = new JTextField(20);
        txtEmailCliente = new JTextField(20);
        txtTelefoneCliente = new JTextField(20);
        JButton btnSalvarCliente = new JButton("Salvar Cliente");

        gbc.gridx = 0; gbc.gridy = 0; painel.add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; painel.add(txtNomeCliente, gbc);

        gbc.gridx = 0; gbc.gridy = 1; painel.add(new JLabel("E-mail:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; painel.add(txtEmailCliente, gbc);

        gbc.gridx = 0; gbc.gridy = 2; painel.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; painel.add(txtTelefoneCliente, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        painel.add(btnSalvarCliente, gbc);

        btnSalvarCliente.addActionListener(e -> salvarCliente());
        return painel;
    }

    private JPanel criarPainelNovoPedido() {
        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbClientes = new JComboBox<>();
        cbProdutos = new JComboBox<>();
        txtQuantidade = new JTextField("1", 5);
        txtValorBruto = new JTextField(10);
        txtValorBruto.setEditable(false);
        txtValorDesconto = new JTextField(10);
        txtValorDesconto.setEditable(false);
        txtValorFinal = new JTextField(10);
        txtValorFinal.setEditable(false);

        btnCalcularDesconto = new JButton("1. Calcular Valor e Desconto (Function)");
        btnCriarPedido = new JButton("2. Confirmar e Gerar Pedido");

        gbc.gridx = 0; gbc.gridy = 0; painel.add(new JLabel("Selecionar Cliente:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; painel.add(cbClientes, gbc);

        gbc.gridx = 0; gbc.gridy = 1; painel.add(new JLabel("Selecionar Produto:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; painel.add(cbProdutos, gbc);

        gbc.gridx = 0; gbc.gridy = 2; painel.add(new JLabel("Quantidade:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; painel.add(txtQuantidade, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        painel.add(btnCalcularDesconto, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 4; painel.add(new JLabel("Subtotal Bruto:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; painel.add(txtValorBruto, gbc);

        gbc.gridx = 0; gbc.gridy = 5; painel.add(new JLabel("Desconto Aplicado (fn_calcular_desconto):"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; painel.add(txtValorDesconto, gbc);

        gbc.gridx = 0; gbc.gridy = 6; painel.add(new JLabel("Valor Total a Pagar:"), gbc);
        gbc.gridx = 1; gbc.gridy = 6; painel.add(txtValorFinal, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        painel.add(btnCriarPedido, gbc);

        btnCalcularDesconto.addActionListener(e -> calcularValoresComFunction());
        btnCriarPedido.addActionListener(e -> salvarPedidoCompleto());

        return painel;
    }

    private JPanel criarPainelDashboard() {
        JPanel painel = new JPanel(new BorderLayout());

        modeloTabela = new DefaultTableModel(new String[]{"ID Pedido", "Cliente", "Data", "Status", "Total Calculado (View)", "Total Registrado"}, 0);
        tabelaView = new JTable(modeloTabela);
        JScrollPane scroll = new JScrollPane(tabelaView);
        scroll.setBorder(BorderFactory.createTitledBorder("Dados em Tempo Real da View: vw_relatorio_pedidos"));
        painel.add(scroll, BorderLayout.CENTER);

        JPanel painelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelInferior.setBorder(BorderFactory.createTitledBorder("Execução de Transação via Procedure: sp_finalizar_pedido"));

        painelInferior.add(new JLabel("ID do Pedido:"));
        txtPedidoIdProcedure = new JTextField(5);
        painelInferior.add(txtPedidoIdProcedure);

        btnExecutarProcedure = new JButton("Executar sp_finalizar_pedido (Dá baixa no estoque e conclui)");
        painelInferior.add(btnExecutarProcedure);

        btnRecarregarView = new JButton("Recarregar View Manualmente");
        painelInferior.add(btnRecarregarView);

        painel.add(painelInferior, BorderLayout.SOUTH);

        btnRecarregarView.addActionListener(e -> carregarDadosView());
        btnExecutarProcedure.addActionListener(e -> executarProcedureFinalizar());

        tabelaView.getSelectionModel().addListSelectionListener(e -> {
            int linha = tabelaView.getSelectedRow();
            if (linha != -1) {
                txtPedidoIdProcedure.setText(tabelaView.getValueAt(linha, 0).toString());
            }
        });

        return painel;
    }

    private void salvarCliente() {
        String sql = "INSERT INTO clientes (nome, email, telefone) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoPostgres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, txtNomeCliente.getText().trim());
            stmt.setString(2, txtEmailCliente.getText().trim());
            stmt.setString(3, txtTelefoneCliente.getText().trim());
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!");
            txtNomeCliente.setText("");
            txtEmailCliente.setText("");
            txtTelefoneCliente.setText("");
            recarregarCombos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar cliente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calcularValoresComFunction() {
        ItemCombo produto = (ItemCombo) cbProdutos.getSelectedItem();
        if (produto == null) return;

        try {
            int qtd = Integer.parseInt(txtQuantidade.getText().trim());
            BigDecimal valorBruto = produto.preco.multiply(new BigDecimal(qtd));
            txtValorBruto.setText("R$ " + valorBruto);

            String sql = "SELECT fn_calcular_desconto(?) AS desconto";
            try (Connection conn = ConexaoPostgres.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setBigDecimal(1, valorBruto);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        BigDecimal desconto = rs.getBigDecimal("desconto");
                        BigDecimal totalFinal = valorBruto.subtract(desconto);

                        txtValorDesconto.setText("R$ " + desconto);
                        txtValorFinal.setText("R$ " + totalFinal);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro no cálculo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarPedidoCompleto() {
        ItemCombo cliente = (ItemCombo) cbClientes.getSelectedItem();
        ItemCombo produto = (ItemCombo) cbProdutos.getSelectedItem();

        if (cliente == null || produto == null || txtValorFinal.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Calcule os valores antes de confirmar o pedido.");
            return;
        }

        String sqlPedido = "INSERT INTO pedidos (cliente_id, status, valor_total) VALUES (?, 'PENDENTE', ?) RETURNING id";
        String sqlItem = "INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoPostgres.getConnection()) {
            conn.setAutoCommit(false);

            int pedidoId = 0;
            BigDecimal valorTotal = new BigDecimal(txtValorFinal.getText().replace("R$", "").trim().replace(",", "."));

            try (PreparedStatement stmtP = conn.prepareStatement(sqlPedido)) {
                stmtP.setInt(1, cliente.id);
                stmtP.setBigDecimal(2, valorTotal);
                ResultSet rs = stmtP.executeQuery();
                if (rs.next()) {
                    pedidoId = rs.getInt(1);
                }
            }

            int qtd = Integer.parseInt(txtQuantidade.getText().trim());
            try (PreparedStatement stmtI = conn.prepareStatement(sqlItem)) {
                stmtI.setInt(1, pedidoId);
                stmtI.setInt(2, produto.id);
                stmtI.setInt(3, qtd);
                stmtI.setBigDecimal(4, produto.preco);
                stmtI.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Pedido #" + pedidoId + " criado como PENDENTE com sucesso!");
            
            carregarDadosView();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gravar pedido: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void carregarDadosView() {
        modeloTabela.setRowCount(0);
        String sql = "SELECT * FROM vw_relatorio_pedidos";

        try (Connection conn = ConexaoPostgres.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                modeloTabela.addRow(new Object[]{
                    rs.getInt("pedido_id"),
                    rs.getString("cliente_nome"),
                    rs.getTimestamp("data_pedido"),
                    rs.getString("status"),
                    "R$ " + rs.getBigDecimal("total_calculado"),
                    "R$ " + rs.getBigDecimal("total_registrado")
                });
            }
        } catch (SQLException ex) {
            System.err.println("Erro ao carregar View: " + ex.getMessage());
        }
    }

    private void executarProcedureFinalizar() {
        if (txtPedidoIdProcedure.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Indique o ID do pedido ou selecione uma linha da tabela.");
            return;
        }

        String sql = "CALL sp_finalizar_pedido(?)";

        try (Connection conn = ConexaoPostgres.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            int pedidoId = Integer.parseInt(txtPedidoIdProcedure.getText().trim());
            stmt.setInt(1, pedidoId);
            stmt.execute();

            JOptionPane.showMessageDialog(this, "Procedure executada com sucesso!\nO pedido #" + pedidoId + " foi marcado como CONCLUIDO e o estoque foi reduzido.");
            carregarDadosView();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro na Procedure: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void recarregarCombos() {
        cbClientes.removeAllItems();
        cbProdutos.removeAllItems();

        try (Connection conn = ConexaoPostgres.getConnection()) {
            Statement stCli = conn.createStatement();
            ResultSet rsCli = stCli.executeQuery("SELECT id, nome FROM clientes ORDER BY nome");
            while (rsCli.next()) {
                cbClientes.addItem(new ItemCombo(rsCli.getInt("id"), rsCli.getString("nome"), BigDecimal.ZERO));
            }

            Statement stProd = conn.createStatement();
            ResultSet rsProd = stProd.executeQuery("SELECT id, nome, preco_unitario FROM produtos ORDER BY nome");
            while (rsProd.next()) {
                cbProdutos.addItem(new ItemCombo(rsProd.getInt("id"), rsProd.getString("nome"), rsProd.getBigDecimal("preco_unitario")));
            }
        } catch (SQLException ex) {
            System.err.println("Erro ao carregar seletores: " + ex.getMessage());
        }
    }

    private static class ItemCombo {
        int id;
        String rotulo;
        BigDecimal preco;

        public ItemCombo(int id, String rotulo, BigDecimal preco) {
            this.id = id;
            this.rotulo = rotulo;
            this.preco = preco;
        }

        @Override
        public String toString() {
            if (preco.compareTo(BigDecimal.ZERO) > 0) {
                return rotulo + " (R$ " + preco + ")";
            }
            return rotulo;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}
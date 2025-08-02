package Services;

import DataBase.DBManager;
import Entities.Pedido;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PedidoService {

    public static void salvarPedido(Pedido pedido) {
        String sql = "INSERT INTO pedidos (produto, quantidade, preco, data, cliente_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBManager.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pedido.getProduto());
            stmt.setInt(2, pedido.getQuantidade());
            stmt.setDouble(3, pedido.getPreco());
            stmt.setDate(4, Date.valueOf(pedido.getData()));
            stmt.setInt(5, pedido.getClienteId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Pedido> buscarPedidos(String nomeCliente, LocalDate dataMinima) {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT p.* FROM pedidos p JOIN clientes c ON p.cliente_id = c.id WHERE c.nome = ? AND p.data >= ?";
        try (Connection conn = DBManager.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeCliente);
            stmt.setDate(2, Date.valueOf(dataMinima));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Pedido pedido = new Pedido(
                        rs.getString("produto"),
                        rs.getInt("quantidade"),
                        rs.getDouble("preco"),
                        rs.getDate("data").toLocalDate(),
                        rs.getInt("cliente_id")
                );
                lista.add(pedido);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}

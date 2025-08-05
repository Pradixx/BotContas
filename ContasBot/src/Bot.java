import DataBase.DBManager;
import Entities.Pedido;
import Services.PedidoService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Bot extends TelegramLongPollingBot {

    private final Map<Long, String> estados = new HashMap<>();
    private final Map<String, String> buffer = new HashMap<>();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public String getBotUsername() {
        return "Cleo Contas bot";
    }

    @Override
    public String getBotToken() {
        return "TOKEN";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String msg = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        try {
            if (msg.equals("/registrar")) {
                estados.put(chatId, "esperando_nome");
                sendMsg(chatId, "Digite o nome do cliente:");
            } else if (msg.equals("/conta")) {
                estados.put(chatId, "conta_nome");
                sendMsg(chatId, "Digite o nome do cliente:");
            } else if (estados.containsKey(chatId)) {
                processaFluxo(chatId, msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendMsg(chatId, "Erro interno.");
        }
    }

    private void processaFluxo(Long chatId, String msg) {
        String estado = estados.get(chatId);
        switch (estado) {
            case "esperando_nome" -> {
                int idCliente = obterOuCriarCliente(msg);
                buffer.put(chatId.toString(), String.valueOf(idCliente));
                estados.put(chatId, "produto");
                sendMsg(chatId, "Digite o nome do produto:");
            }
            case "produto" -> {
                buffer.put(chatId + "_produto", msg);
                estados.put(chatId, "quantidade");
                sendMsg(chatId, "Digite a quantidade:");
            }
            case "quantidade" -> {
                buffer.put(chatId + "_quantidade", msg);
                estados.put(chatId, "preco");
                sendMsg(chatId, "Digite o preço unitário:");
            }
            case "preco" -> {
                buffer.put(chatId + "_preco", msg);
                estados.put(chatId, "data");
                sendMsg(chatId, "Digite a data (dd/MM/yyyy):");
            }
            case "data" -> {
                try {
                    int clienteId = Integer.parseInt(buffer.get(chatId.toString()));
                    String produto = buffer.get(chatId + "_produto");
                    int quantidade = Integer.parseInt(buffer.get(chatId + "_quantidade"));
                    double preco = Double.parseDouble(buffer.get(chatId + "_preco"));
                    LocalDate data = LocalDate.parse(msg, formatter);

                    Pedido pedido = new Pedido(produto, quantidade, preco, data, clienteId);
                    PedidoService.salvarPedido(pedido);

                    sendMsg(chatId, "✅ Pedido registrado com sucesso!");
                    estados.remove(chatId);
                } catch (Exception e) {
                    sendMsg(chatId, "❌ Erro ao salvar. Verifique os dados e tente novamente.");
                    e.printStackTrace();
                }
            }
            case "conta_nome" -> {
                buffer.put(chatId + "_cliente_nome", msg);
                estados.put(chatId, "conta_data");
                sendMsg(chatId, "Digite a data inicial (dd/MM/yyyy):");
            }
            case "conta_data" -> {
                try {
                    String nome = buffer.get(chatId + "_cliente_nome");
                    LocalDate data = LocalDate.parse(msg, formatter);
                    List<Pedido> pedidos = PedidoService.buscarPedidos(nome, data);

                    if (pedidos.isEmpty()) {
                        sendMsg(chatId, "Nenhum pedido encontrado.");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        double total = 0;

                        sb.append("📦 Histórico de Pedidos:\n\n");

                        for (Pedido p : pedidos) {
                            double subtotal = p.getPreco() * p.getQuantidade();
                            total += subtotal;
                            sb.append(String.format(
                                    "🛒 Produto: %s\n📦 Quantidade: %d\n💲 Preço Unitário: R$ %.2f\n📅 Data: %s\nSubtotal: R$ %.2f\n\n",
                                    p.getProduto(), p.getQuantidade(), p.getPreco(),
                                    p.getData().format(formatter), subtotal
                            ));
                        }

                        sb.append(String.format("💰 *Total da Conta:* R$ %.2f", total));

                        SendMessage resposta = new SendMessage(chatId.toString(), sb.toString());
                        resposta.setParseMode("Markdown");
                        execute(resposta);
                    }

                    estados.remove(chatId);
                } catch (Exception e) {
                    sendMsg(chatId, "Erro ao buscar pedidos. Verifique a data no formato dd/MM/yyyy.");
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendMsg(Long chatId, String text) {
        SendMessage msg = new SendMessage(chatId.toString(), text);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private int obterOuCriarCliente(String nome) {
        try (Connection conn = DBManager.connect()) {
            PreparedStatement find = conn.prepareStatement("SELECT id FROM clientes WHERE nome = ?");
            find.setString(1, nome);
            ResultSet rs = find.executeQuery();
            if (rs.next()) return rs.getInt("id");

            PreparedStatement insert = conn.prepareStatement("INSERT INTO clientes (nome) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            insert.setString(1, nome);
            insert.executeUpdate();
            ResultSet generated = insert.getGeneratedKeys();
            if (generated.next()) return generated.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}

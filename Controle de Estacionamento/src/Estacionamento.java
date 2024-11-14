import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

public class Estacionamento extends JFrame implements ActionListener {

    // variáveis globais
    private JTextField placaTextField;
    private JTextField horarioEntradaTextField;
    private JTextField horarioSaidaTextField;
    private JLabel resultadoLabel;
    String imagePath = "C:/Users/amand/Music/Estacionamento-JFrame/img/initialScreen.svg";

    // variáveis sobre o cálculo da tarifa
    private final double TARIFA_ATE_3_HORAS = 10.00; // substitua por "X,XX"
    private final double TARIFA_EXTRA = 5.00; // substitua por "Y,YY"
    private final int TOLERANCIA_MINUTOS = 15;

    // construtor
    public Estacionamento() {
        // configurações iniciais da tela JFrame
        setTitle("Estacionamento");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // painel esquerdo
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(0, 128, 128));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // foto do carro
        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        ImageIcon icon = new ImageIcon("Controle de Estacionamento\\src\\img\\parksmart.png");
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(290, 390, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaledImage));
        leftPanel.add(logoLabel);

        // painel direito
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Inicialização dos campos de texto e botão lado direito
        placaTextField = createStyledTextField("Insira a placa do carro");
        horarioEntradaTextField = createStyledTextField("Horário de entrada (HH:mm)");
        horarioSaidaTextField = createStyledTextField("Horário de saída (HH:mm)");
        
        resultadoLabel = new JLabel("Tarifa: R$ 0,00");
        resultadoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton enviarButton = new JButton("ENVIAR");
        enviarButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        enviarButton.setBackground(new Color(0, 128, 128));
        enviarButton.setForeground(Color.WHITE);
        enviarButton.addActionListener(this);
        

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(280); // Define a posição da divisão
        splitPane.setDividerSize(0);
        splitPane.setEnabled(false); // Desabilita redimensionamento manual do usuário

        // Adicionando os componentes à janela
        rightPanel.add(Box.createRigidArea(new Dimension(0, 60)));
        rightPanel.add(placaTextField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(horarioEntradaTextField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(horarioSaidaTextField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(Box.createHorizontalGlue()); // Adiciona espaço flexível antes do botão
        rightPanel.add(enviarButton);
        rightPanel.add(Box.createHorizontalGlue());

        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(resultadoLabel);

        add(splitPane);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        calcularTarifa(); // Chamando o método de cálculo quando o botão é pressionado
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        textField.setForeground(Color.GRAY);
        textField.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 102), 2, true));
        textField.setHorizontalAlignment(JTextField.CENTER);
        return textField;
    }

    private void calcularTarifa() {
        try {
            // Formato de hora
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            // Ler as horas de entrada e saída
            LocalTime entrada = LocalTime.parse(horarioEntradaTextField.getText(), formatter);
            LocalTime saida = LocalTime.parse(horarioSaidaTextField.getText(), formatter);

            // Calcular a diferença em minutos
            long minutos = Duration.between(entrada, saida).toMinutes();

            double tarifa = 0.0;

            // Aplicar a regra da tolerância
            if (minutos <= TOLERANCIA_MINUTOS) {
                tarifa = 0.0;
            } else if (minutos <= 180) { // Até três horas
                tarifa = TARIFA_ATE_3_HORAS;
            } else {
                tarifa = TARIFA_ATE_3_HORAS;
                // Calcular horas adicionais após as três primeiras
                long horasAdicionais = (minutos - 180) / 60 + 1; // Contabilizar frações
                tarifa += horasAdicionais * TARIFA_EXTRA;
            }

            // Atualizar o label com o valor da tarifa
            resultadoLabel.setText(String.format("Tarifa: R$ %.2f", tarifa));

            // Verificar a placa
            String placa = placaTextField.getText().toUpperCase();
            if (verificarPlacaPorEstado(placa)) {
                resultadoLabel.setText(resultadoLabel.getText() + " | Placa válida para o estado.");
            } else {
                resultadoLabel.setText(resultadoLabel.getText() + " | Placa inválida para o estado.");
            }

        } catch (Exception ex) {
            resultadoLabel.setText("Erro: formato inválido");
        }
    }

    // Método para verificar a placa
    private boolean verificarPlacaPorEstado(String placa) {
        // Verifica se a placa tem 3 caracteres e são letras
        if (placa.length() < 3 || !placa.substring(0, 3).matches("[A-Za-z]+")) {
            return false;
        }

        // Primeiras três letras da placa
        String prefixo = placa.substring(0, 3).toUpperCase();

        // Definindo os intervalos das placas para cada estado
        return (prefixo.compareTo("AAA") >= 0 && prefixo.compareTo("BEZ") <= 0) || // Paraná
               (prefixo.compareTo("IAQ") >= 0 && prefixo.compareTo("JDO") <= 0) || // Rio Grande do Sul
               (prefixo.compareTo("LWR") >= 0 && prefixo.compareTo("MMM") <= 0);   // Santa Catarina
    }

    public static void main(String[] args) {
        Estacionamento estacionamento = new Estacionamento();
        estacionamento.setVisible(true);
    }
}

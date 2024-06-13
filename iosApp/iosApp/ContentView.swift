import SwiftUI
import shared

struct ContentView: View {
    let calculator = Calculator.Companion()
    
    @State private var displayValue: String = "0"
    @State private var firstNumber: Int32? = nil
    @State private var secondNumber: Int32? = nil
    @State private var currentOperation: String? = nil
    
    let buttonValues: [[String]] = [
        ["AC", "+/-", "%", "÷"],
        ["7", "8", "9", "×"],
        ["4", "5", "6", "-"],
        ["1", "2", "3", "+"],
        ["0", ".", "="]
    ]
    
    var body: some View {
        VStack(spacing: 5) {
            VStack {
                Image(systemName: "checkerboard.shield")
                    .foregroundColor(.primary)
                    .font(.system(size: 40))

                HStack {
                    Text("Calculator")
                        .foregroundColor(.primary)
                        .font(.largeTitle)
                        .bold()
                }
  
                Text("Using Kotlin Multiplatform")
                    .foregroundColor(.secondary)
                    .grayscale(1)
                    .font(.subheadline)
            }
            Spacer()
            Text(displayValue)
                .font(.system(size: 72))
                .foregroundColor(.primary)
                .padding()
                .frame(maxWidth: .infinity, alignment: .trailing)
            
            ForEach(buttonValues, id: \.self) { row in
                HStack(spacing: 5) {
                    ForEach(row, id: \.self) { button in
                        Button(action: {
                            self.buttonTapped(button)
                        }) {
                            Text(button)
                                .frame(width: self.buttonWidth(button) - 20, height: self.buttonHeight() - 20)
                                   .foregroundColor(.primary)
                        }
                        .font(.title)
                        .foregroundColor(.primary)
                        .buttonBorderShape(.roundedRectangle)
                        .buttonStyle(.bordered)
                    }
                }
            }
        }
        .padding()
    }
    
    private func buttonTapped(_ button: String) {
        switch button {
        case "AC":
            clear()
        case "+/-":
            toggleSign()
        case "%":
            percentage()
        case "÷", "×", "-", "+":
            operation(button)
        case "=":
            calculateResult()
        case ".":
            addDecimalPoint()
        default:
            addNumber(button)
        }
    }
    
    private func clear() {
        displayValue = "0"
        firstNumber = nil
        secondNumber = nil
        currentOperation = nil
    }
    
    private func toggleSign() {
        if let value = Int32(displayValue) {
            displayValue = String(value * -1)
        }
    }
    
    private func percentage() {
        if let value = Double(displayValue) {
            displayValue = String(value / 100)
        }
    }
    
    private func operation(_ operation: String) {
        if let value = Int32(displayValue) {
            firstNumber = value
            displayValue = "0"
            currentOperation = operation
        }
    }
    
    private func calculateResult() {
        if let operation = currentOperation, let firstNumber = firstNumber, let value = Int32(displayValue) {
            secondNumber = value
            let result: Int32
            switch operation {
            case "+":
                result = calculator.sum3(a: firstNumber, b: value)
            case "-":
                result = calculator.minus(a: firstNumber, b: value)
            case "×":
                result = calculator.multiply(a: firstNumber, b: value)
            case "÷":
                result = calculator.divide(a: firstNumber, b: value)
            default:
                return
            }
            displayValue = String(result)
            self.firstNumber = nil
            self.secondNumber = nil
            self.currentOperation = nil
        }
    }
    
    private func addNumber(_ number: String) {
        if displayValue == "0" {
            displayValue = number
        } else {
            displayValue += number
        }
    }
    
    private func addDecimalPoint() {
        if !displayValue.contains(".") {
            displayValue += "."
        }
    }
    
    private func buttonWidth(_ button: String) -> CGFloat {
        if button == "0" {
            return ((UIScreen.main.bounds.width - 5 * 12) / 4) * 2
        }
        return (UIScreen.main.bounds.width - 5 * 12) / 4
    }
    
    private func buttonHeight() -> CGFloat {
        return (UIScreen.main.bounds.width - 5 * 12) / 4
    }
    
    private func buttonColor(_ button: String) -> Color {
        switch button {
        case "AC", "+/-", "%":
            return Color.gray
        case "÷", "×", "-", "+", "=":
            return Color.orange
        default:
            return Color(.darkGray)
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}

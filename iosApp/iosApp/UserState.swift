import Foundation
import shared
import FirebaseCore
import FirebaseAuth
import GoogleSignIn


class UserState: ObservableObject {
    @Published var user: shared.User? = nil
    var authManager = AuthManager()
    @Published var startScreen : Screen = Screen.LoginScreen()

    init() {
        observeUser()
    }
    

    private func observeUser() {
        Task {
            for await _user in authManager.user {
                DispatchQueue.main.async {
                    self.user = _user
                    self.startScreen = if(self.authManager.isLoggedIn()) {
                        Screen.CalculatorScreen()
                     } else {
                        Screen.LoginScreen()
                   }
                }
            }
        }
    }
    
    func login() {
        guard let clientID = FirebaseApp.app()?.options.clientID else {return}
        let config = GIDConfiguration(clientID: clientID)
        GIDSignIn.sharedInstance.configuration = config
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene else {return}
        guard let rootViewController = windowScene.windows.first?.rootViewController else {return}

        GIDSignIn.sharedInstance.signIn(withPresenting: rootViewController) { [unowned self] result, error in
            guard error == nil else {
                return
            }

            guard let resultUser = result?.user,
                  let idToken = resultUser.idToken?.tokenString else {return}
            let accessToken = resultUser.accessToken.tokenString

            Task {
                do {
                    try await authManager.login(idToken: idToken, accessToken : accessToken) {
                        DispatchQueue.main.async {
                            self.startScreen = Screen.CalculatorScreen()
                        }
                    }
                } catch {
                    print("Error during login: \(error)")
                }
            }
        }
    }
    
    func logout() {
        Task {
            try await authManager.logout()
            self.startScreen = Screen.LoginScreen()
        }
    }
}

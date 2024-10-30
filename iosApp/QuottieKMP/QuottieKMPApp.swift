import SwiftUI
import ComposeApp

@main
struct QuottieKMPApp: App {

    init() {
        KoinHelperKt.doInitKoin()
        LogKt.debugBuild()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

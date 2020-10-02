//  MIT License

//  Copyright (c) 2019 Haik Aslanyan

//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:

//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.

//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//  SOFTWARE.

import UIKit

class InitialViewController: UIViewController {
  
  override var preferredStatusBarStyle: UIStatusBarStyle {
    return .lightContent
  }
  
  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    
    let isNotLogined = UserManager().currentUserID().isNone
    var vc: UIViewController
    if isNotLogined {
      vc = UIStoryboard.initial(storyboard: .auth)
    } else {
        
      /// MApViewController
      let mapVC = MapViewController()
      let mapItem = UITabBarItem()
      mapItem.title = "GPSocial"
      mapItem.image = UIImage(named: "location-pointer")
      mapVC.tabBarItem = mapItem

      /// Chat
      let chatVC = UIStoryboard.initial(storyboard: .conversations)
      let chatBoxItem = UITabBarItem()
      chatBoxItem.title = "Chat Box"
      chatBoxItem.image = UIImage(named: "chat")
      chatVC.tabBarItem = chatBoxItem
      
      
      /// Info
      let infoVC = IPInforViewController()
      let infoItem = UITabBarItem()
      infoItem.title = "Detail"
      infoItem.image = UIImage(named: "info")
      infoVC.tabBarItem = infoItem
      
      
      let tabBarController = UITabBarController()
      tabBarController.viewControllers = [mapVC, chatVC, infoVC]
      vc = UINavigationController(rootViewController: tabBarController)
    }
    
    vc.modalPresentationStyle = .fullScreen
    present(vc, animated: true)
  }
}

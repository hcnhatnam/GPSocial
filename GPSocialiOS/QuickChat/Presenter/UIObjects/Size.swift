//
//  Size.swift
//  ColorNinja
//
//  Created by Do Le Duy on 5/9/20.
//  Copyright © 2020 Do Le Duy. All rights reserved.
//

import UIKit

struct Size {

    static var statusBarHeight: CGFloat {
        if UIApplication.shared.isStatusBarHidden {
            return 0
        }
        return UIApplication.shared.statusBarFrame.size.height
    }

    static func navigationBarHeight(_ viewController: UIViewController) -> CGFloat {
        return viewController.navigationController?.navigationBar.frame.size.height ?? CGFloat(0)
    }

    static func appBarHeight(_ viewController: UIViewController) -> CGFloat {
        return statusBarHeight + navigationBarHeight(viewController)
    }

    static func screenRectWithoutAppBar(_ viewController: UIViewController) -> CGRect {
        let appBarHeight: CGFloat = {
            if #available(iOS 11.0, *) {
                return viewController.view.safeAreaInsets.top
            } else {
                return Size.appBarHeight(viewController)
            }
        }()
        return CGRect(
            x: 0,
            y: appBarHeight,
            width: UIScreen.main.bounds.width,
            height: UIScreen.main.bounds.height - appBarHeight)
    }
}

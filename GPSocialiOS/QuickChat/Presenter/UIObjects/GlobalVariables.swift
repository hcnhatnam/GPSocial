//
//  GlobalVariables.swift
//  QuickChat
//
//  Created by Do Le Duy on 9/20/20.
//  Copyright © 2020 Haik Aslanyan. All rights reserved.
//

import Foundation

let defaultAvatarName = "profile pic"
let homeIconName = "location-pin"

import UIKit

func ColorRGB(_ red: CGFloat, _ green: CGFloat,_ blue: CGFloat ) -> UIColor {
  return UIColor(red: red/255, green: green/255, blue: blue/255, alpha: 1)
}

func printAllFamilyFonts() {
  for family in UIFont.familyNames.sorted() {
    let names = UIFont.fontNames(forFamilyName: family)
    print("Family: \(family) Font Names: \(names)")
  }
}

func getRealNameWithoutPlus(name: String) -> String {
  return name.replacingOccurrences(of: "+", with: " ", options: .literal, range: nil)
}

func bottomPadding() -> CGFloat {
  let padding: CGFloat = 10
  let bottomPadding = safeAreaBottom() > 0 ? safeAreaBottom() : padding
  
  return bottomPadding
}

func getDeviceId() -> String {
  return UIDevice.current.identifierForVendor!.uuidString
}

func notEmptyString(string: String) -> Bool {
  return string != ""
}

let ScreenSize = UIScreen.main.bounds.size


public func fontSizeScaled(_ value: CGFloat) -> CGFloat {
    let scaled = scaledValue(value)
    return scaled.rounded(.up)
}

public func scaledValue(_ value: CGFloat,_ baseWidth: CGFloat = 414) -> CGFloat {
    var multiplier = UIScreen.main.bounds.width / baseWidth
    
    if multiplier > 1.2 {
        multiplier = 1.2
    }
    
    let scaled = value * multiplier
    
    return scaled.rounded()
}

public func sizeScalediPhone5(_ value: CGFloat) -> CGFloat {
    let scaled = scaledValue(value, 320)
    return scaled.rounded()
}

public func valueScaledHeight(_ value: CGFloat,_ baseHeight: CGFloat = 414) -> CGFloat {
    let multiplier = UIScreen.main.bounds.height / baseHeight
    let scaled = value * multiplier
    
    return scaled.rounded()
}

public func fontSizeScaled(_ value: CGFloat, _ baseWidth: CGFloat = 414) -> CGFloat {
    let scaled = scaledValue(value, baseWidth)
    return scaled.rounded(.up)
}

public func fontSizeScalediPhone5(_ value: CGFloat) -> CGFloat {
    let scaled = scaledValue(value, 320)
    return scaled.rounded(.up)
}

public func isCurrentDeviceInstalled(app appDeepLink: String = "zalo://app") -> Bool {
    guard let appUrl = URL(string: appDeepLink) else {
        return false
    }
    return UIApplication.shared.canOpenURL(appUrl as URL)
}

public func saveArrayIntToUserDefault(_ array:[Int], key: String) {
    let defaults = UserDefaults.standard
    defaults.set(array, forKey: key)
}

public func getArrayIntFromUserDefault(key: String) -> [Int]{
    let defaults = UserDefaults.standard
    return defaults.array(forKey: key)  as? [Int] ?? [Int]()
}

public func safeAreaBottom() -> CGFloat {
    var bottomPadding: CGFloat = 0.0
    if #available(iOS 11.0, *) {
        let window = UIApplication.shared.keyWindow
        bottomPadding = window?.safeAreaInsets.bottom ?? 0.0
    }
    return bottomPadding
}

public func heightStatusBar() -> CGFloat {
    let frame = UIApplication.shared.statusBarFrame
    let topPadding = frame.origin.y + frame.size.height
    return topPadding
}

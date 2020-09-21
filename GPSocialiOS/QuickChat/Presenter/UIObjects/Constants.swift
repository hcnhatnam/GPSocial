//
//  Constants.swift
//  ColorNinja
//
//  Created by Do Le Duy on 5/8/20.
//  Copyright © 2020 Do Le Duy. All rights reserved.
//

import Foundation
import UIKit

struct Font {
  static let snesitalic = "SNES-Italic"
  static let squirk = "Squirk"
  static let dincondenseBold = "DINCondensed-Bold"
}

struct Color {
  struct Zalo {
    static let blue1 = ColorRGB(0,174,255)      // màu xanh đậm zalo
    static let blue2 = ColorRGB(3, 142, 241)    // màu xanh nhạt zalo
  }
  struct Facebook {
    static let loginButton = ColorRGB(60, 102, 196)
  }
  
}

struct Constants {
  
  struct Screen {
    static let width = UIScreen.main.bounds.size.width
    static let height = UIScreen.main.bounds.size.height
    static let size = UIScreen.main.bounds.size
  }
  
  struct HomeScreen {
    static let paddingTopOfIcon : CGFloat = Size.statusBarHeight + 100
    static let ninjaImageName = "appicon"
    static let appName = "Color Ninja"
    static let appNameColor : UIColor = .white
    static let iconWidth : CGFloat = 100
    static let avatarWidth : CGFloat = 100
    static let standardPadding : CGFloat = 10
    static let appNameLabelHeight : CGFloat = 50
    static let appNameFontSize : CGFloat = 50
    static let buttonFontSize : CGFloat = 40
    static let buttonWidth : CGFloat = 200
    static let backgroundColor = ColorRGB(255, 44, 44)
  }
  
  struct GameScreen {
    static let backgroundColor = NiceColor.backgroundColor1
    static let forcegroundColor = NiceColor.backgroundColor2
    static let topInset : CGFloat = Size.statusBarHeight + 20
    static let leftInset : CGFloat = 20
    static let rightInset : CGFloat = 20
    static let settingButtonWidth : CGFloat = 40
    static let settingImageName = "setting1"
    static let exitImageName = "close"
    static let exitButtonWidth = GameScreen.settingButtonWidth
    static let buttonTintColor = LytoColor.settingButtonColor
    
    struct LabelsContainer {
      static let fontSize : CGFloat = 25
      static let textColor : UIColor = LytoColor.labelColorInGame
      static let height : CGFloat = 100
      static let padding : UIEdgeInsets = UIEdgeInsets(top: 10,
                                                       left: 10,
                                                       bottom: 10,
                                                       right: 10)
      static let margins : UIEdgeInsets = UIEdgeInsets(top: 0,
                                                       left: 0,
                                                       bottom: 0,
                                                       right: 0)
    }
    
    struct ReadyView {
      static let textColor : UIColor = .yellow
      static let fontSize : CGFloat = 180
    }
    
    struct BoardCollectionView {
      static let cellId : String = "colorCollectionViewCellIdentifier"
      static var boardWidth = Constants.Screen.width - 2*Constants.GameScreen.leftInset - 50 > 500 ? 500 : Constants.Screen.width - 2*Constants.GameScreen.leftInset - 50
      static let spacingBetweenCells : CGFloat = 5
    }
  }
  
  struct GameSetting {
    static let maxLevelCount = 1000
    static let maxRemainTime = 2.0
  }
  
  struct GameOverPopup {
    static let contentSize = CGSize(width: 4*Constants.Screen.width/5, height: Constants.Screen.height/2)
  }
  
  struct GameSettingPopup {
    static let width = 250
    static let height = width
    static let contentSize = CGSize(width: Constants.GameSettingPopup.width, height: Constants.GameSettingPopup.height)
    static let cornerRadius: CGFloat = 35
    static let navigationBarHeight: CGFloat = 70
  }
  
  struct InputUserNamePopup {
    static let width = 250
    static let height = 80
    static let contentSize = CGSize(width: Constants.InputUserNamePopup.width, height: Constants.InputUserNamePopup.height)
    static let cornerRadius: CGFloat = 15
  }
  
  struct PopupViewController {
    static let defaultCornerRadius: CGFloat = 16
  }
}

/**
 * Link: https://www.pinterest.com/pin/95842298307867764/
 */
struct BluePalletes {
  static let level0 = ColorRGB(225,245,254)
  static let level1 = ColorRGB(179,229,252)
  static let level2 = ColorRGB(129,212,250)
}

struct LytoColor {
  static let gameBGColor = ColorRGB(42, 34, 53)
  static let settingButtonColor = ColorRGB(112, 102, 135)
  static let labelColorInGame = ColorRGB(170, 68, 119)
}

struct NiceColor {
  static let backgroundColor1 = ColorRGB(17, 18, 45)
  static let backgroundColor2 = ColorRGB(109, 133, 167)
}

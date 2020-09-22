//
//  OwnerInfo.swift
//  ColorNinja
//
//  Created by Do Le Duy on 5/22/20.
//  Copyright © 2020 Do Le Duy. All rights reserved.
//

import Foundation
import Alamofire

fileprivate let kUserPassword = "kUserPassword"

class OwnerInfo {
  
  static let shared = OwnerInfo()
  
  //MARK: Readonly Prop

  public private(set) var password: String?
  
  //MARK: Public
  
  func clearPasswordInUserDefault() {
    userDefault.setValue(nil, forKey: kUserPassword)
  }
  
  func didRegisterWithPassword(newPassword: String) {
    
    // UpdateMemory
    password = newPassword
    
    // Update LocalDisk
    userDefault.set(password,forKey: kUserPassword)
  }

  // MARK: Private
  
  private let userDefault = UserDefaults.standard
  
  private init() {
    _loadUserInfo()
  }
  
  private func _loadUserInfo() {
    
    // Password
    if let password = userDefault.string(forKey: kUserPassword) {
      self.password = password
    }
  }
}

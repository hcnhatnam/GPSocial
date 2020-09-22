//
//  LDUserManager.swift
//  QuickChat
//
//  Created by Do Le Duy on 9/21/20.
//  Copyright © 2020 Haik Aslanyan. All rights reserved.
//

import Foundation
import Alamofire

typealias ErrorMessage = String
let serverUrl = "http://35.198.220.200:8765"
let successCode = 0

class LDUserManager {
  
  func resigerUser(user: ObjectUser,completion: ((String?) -> ())?) {
    
    guard let email = user.email, let password = user.password else { completion?("Email nil or password nil"); return }
    
    let parameters: Dictionary<String, Any> = [
      "email": email,
      "username": email,
      "profilepiclink": user.profilePicLink ?? "",
      "password": password
    ]
    
    let registerUserUrl = "\(serverUrl)/user/register/"
    AF.request(registerUserUrl, method: .post, parameters:parameters).responseJSON { (response) in
      if let json = response.value as! [String : Any]? {
        if let errorcode = json["error"] as? Int {
          if errorcode == successCode {
            completion?(nil)
          } else {
            completion?("Failed")
          }
        } else {
          completion?("Failed")
        }
      } else {
        completion?("Request failed")
      }
    }
  }
    
  func login(user: ObjectUser, completion: ((String?) -> ())?) {
    if let password = OwnerInfo.shared.password {
      user.password = password
    } else {
      /// Tại sao đéo có password in UserDefault mà login làm gì?
      fatalError()
    }
    
    guard let email = user.email, let password = user.password else { completion?("Login failed: Email nil or password nil"); return }

    let loginUrl = "\(serverUrl)/user/login/"
    let parameters: Dictionary<String, Any> = [
      "email": email,
      "password": password
    ]
    AF.request(loginUrl, method: .post, parameters: parameters).responseJSON { (response) in
      if let json = response.value as? [String:Any] {
        if let errorCode = json["error"] as? Int {
          if errorCode == successCode {
            print("duydl: Login Nam thành công")
            self.startPingToServer()
            completion?(nil)
          } else {
            completion?("Login Failed")
          }
        }
      } else {
        completion?("Login Failed")
      }
    }
  }
  
  func startPingToServer() {
    let interval: Double = 5
    Timer.scheduledTimer(withTimeInterval: interval, repeats: true) { _ in
      self._ping()
    }
  }
  
  func _ping() {
    guard let email = OwnerInfo.shared.email else {
      assert(false)
      return
    }
    
    let pingUrl = "\(serverUrl)/user/ping/"
    let parameters: Dictionary<String, Any> = [
      "email": email,
    ]
    AF.request(pingUrl, parameters: parameters).response {response in
      switch response.result {
      case .success(let data):
        if let data = data {
          do {
            if let json = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] {
              if let errorCode = json["error"] as? Int {
                if errorCode == successCode {
                  print("Ping success!")
                }
              }
            }
          } catch let error as NSError {
            print(error)
            fatalError()
          }
        }
        
      case .failure(let error):
        print(error)
        fatalError()
      }
    }
  }
}

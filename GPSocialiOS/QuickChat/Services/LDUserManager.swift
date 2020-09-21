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

class LDUserManager {
  
  func resigerUser(user: ObjectUser,completion: ((String?) -> ())?) {
    
    guard let email = user.email, let password = user.password else { completion?("Email nil or password nil"); return }
    
    let parameters: Dictionary<String, Any> = [
      "email": email,    // chú ý refactor chỗ này
      "username": email,
      "profilepiclink": user.profilePicLink ?? "",
      "password": password
    ]
    
    let registerUserUrl = "http://35.198.220.200:8765/user/register/"
    AF.request(registerUserUrl, method: .post, parameters:parameters).responseJSON { (response) in
      if let json = response.value as! [String : Any]? {
        if let errorcode = json["error"] as? Int {
          if errorcode == 0 {
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
}

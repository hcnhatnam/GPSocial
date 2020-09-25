//
//  LDUserManager.swift
//  QuickChat
//
//  Created by Do Le Duy on 9/21/20.
//  Copyright © 2020 Haik Aslanyan. All rights reserved.
//

import Foundation
import Alamofire
import GoogleMaps

typealias ErrorMessage = String
let serverUrl = "http://35.198.220.200:8765"
let successCode = 0
let durationLoopReFetchOnlineUsers: Double = 5

struct UserEntity {
  var id: String?
  var email: String?
  var profilePicLink: String?
  var username: String?
  var position: LDMapPosition?
}


class LDUserManager {
  
  public func resigerUser(user: ObjectUser,completion: ((String?) -> ())?) {
    print("duydl: Register")
    
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
    
  public func login(user: ObjectUser, completion: ((String?) -> ())?) {
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
  
  public func fetchOnlineusers(completion: @escaping (Result<[UserEntity], Error>) -> Void) {
    Timer.scheduledTimer(withTimeInterval: durationLoopReFetchOnlineUsers, repeats: true) { _ in
      self._requestListOnlineUsers(completion: completion)
    }.fire()
  }

  // MARK: Private
  
  private func _requestListOnlineUsers(completion: @escaping (Result<[UserEntity], Error>) -> Void) {
    guard let email = OwnerInfo.shared.email else {
      assert(false)
      return
    }
    
    let pingUrl = "\(serverUrl)/user/onlineusers/"
    let parameters: Dictionary<String, Any> = [
      "email": email,
    ]
    
    AF.request(pingUrl, parameters: parameters).response {response in
      switch response.result {
      case .success(let data):
        guard let data = data else {
          /// Sao đéo có data?
          assert(false)
          return
        }
        
        do {
          if let json = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] {
            if let errorCode = json["error"] as? Int {
              if errorCode == successCode {
                if let data = json["data"] as? [String: Any] {
                  if let onlineusers = data["onlineUsers"] as? [String: Any] {
                    let users = self._parseDataJsonsDictToUserEntities(onlineusers)
                    completion(.success(users))
                  }
                }
              } else {
                assert(false)
              }
            }
          }
        } catch let error as NSError {
          print(error)
          completion(.failure(error))
        }
        
      case .failure(let error):
        completion(.failure(error))
      }
    }
  }
  
  private func _parseDataJsonsDictToUserEntities(_ userjsons: [String: Any]) -> [UserEntity] {
    var userEntities: [UserEntity] = []
    
    for userEmail in userjsons.keys {
      if let userDict = userjsons[userEmail],
         let json = userDict as? [String : Any] {
        let userEntity = _parseToUserEntity(userjson: json)
        userEntities.append(userEntity)
      }
    }
  
    return userEntities
  }
  
  private func _parseToUserEntity(userjson: [String: Any]) -> UserEntity {
    var userEntity = UserEntity()
    
    /// Location
    if let locationInfo = userjson["locationInfo"] as?  [String:Any] {
      var position = LDMapPosition()
      if let lat = locationInfo["latitude"] as? Double {
        position.latitude = lat
      }
      if let long = locationInfo["longitude"] as? Double {
        position.longitude = long
      }
      userEntity.position = position
    }
    
    /// Avatar URL
    if let user = userjson["user"] as? [String:Any] {
      if let avtLink = user["profilePicLink"] as? String {
        if avtLink != "" {
          userEntity.profilePicLink = avtLink
        }
      }
    }
    
    print("Postion: lat: \(String(describing: userEntity.position?.latitude)), long: \(String(describing: userEntity.position?.longitude))")
    return userEntity
  }
}

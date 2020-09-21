//
//  IP2LocationService.swift
//  QuickChat
//
//  Created by Do Le Duy on 9/19/20.
//  Copyright © 2020 Haik Aslanyan. All rights reserved.
//

import Foundation
import Alamofire
import GoogleMaps

typealias requestOwnerIPCompletion = (Result<String, Error>) -> Void
typealias requestCurrentLocationCompletion = (Result<CLLocationCoordinate2D, Error>) -> Void

class IP2LocationService {
  
  func loadOwnerIP(completion: @escaping requestOwnerIPCompletion) {
    let url = "http://35.198.220.200:8765/showip"

    AF.request(url).response {response in
      switch response.result {
      case .success(let data):
        if let data = data {
          do {
            if let json = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] {
              if let data = json["data"] as? [String: Any] {
                if let ip = data["ip"] as? String {
                return completion(.success(ip))
                }
              }
            }
          } catch let error as NSError {
            completion(.failure(error))
          }
        }
      case.failure(let error):
        completion(.failure(error))
      }
    }
  }
  
  func getLocationOfIP(ip: String, completion: @escaping requestCurrentLocationCompletion) {
    let url = "http://35.198.220.200:8765/ip?ip=\(ip)"
    AF.request(url).response { response in
      switch response.result {
      case .success(let data):
        if let data = data {
          do {
            if let json = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] {
              if let data = json["data"] as? [String: Any] {
                if let ips = data["ips"] as? [String: Any] {
                  let latitude = ips["latitude"] as! Double
                  let longitude = ips["longitude"] as! Double
                  return completion(.success(CLLocationCoordinate2D(latitude: latitude, longitude: longitude)))
                }
              }
            }
          } catch let error as NSError {
            completion(.failure(error))
          }
        }
      case .failure(let error):
        completion(.failure(error))
      }
    }
  }
  
}

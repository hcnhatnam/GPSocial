//
//  LDUIImageView.swift
//  ColorNinja
//
//  Created by Do Le Duy on 7/12/20.
//  Copyright © 2020 Do Le Duy. All rights reserved.
//

import Foundation
import UIKit

class LDImageView: UIImageView {
  
  /// Private
  private var task: URLSessionDataTask?
  
  /// Download
  
  public private(set) var downloading: Bool = false

  public func downloadAndSetImageWithLink(from url: URL, contentMode mode: UIView.ContentMode = .scaleAspectFit) {
    contentMode = mode
    task = URLSession.shared.dataTask(with: url) { data, response, error in
      guard
        let httpURLResponse = response as? HTTPURLResponse, httpURLResponse.statusCode == 200,
        let mimeType = response?.mimeType, mimeType.hasPrefix("image"),
        let data = data, error == nil,
        let image = UIImage(data: data)
        else {
          DispatchQueue.main.async() { [weak self] in
            self?.downloading = false
            self?.image = UIImage(named: defaultAvatarName)
          }
          return
      }
      
      DispatchQueue.main.async() { [weak self] in
        self?.image = image
      }
    }
    
    downloading = true
    task?.resume()
  }
  
  public func cancelDownloadImage() {
    if downloading {
      task?.cancel()
    }
  }
}

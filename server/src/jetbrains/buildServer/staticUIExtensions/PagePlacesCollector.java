/*
 * Copyright 2000-2011 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.staticUIExtensions;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.web.PagePlacesEx;
import jetbrains.buildServer.web.openapi.PlaceId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 16.11.11 18:14
 */
public class PagePlacesCollector {
  private static final Logger LOG = Logger.getInstance(PagePlacesCollector.class.getName());

  private final PagePlacesEx myPagePlaces;

  public PagePlacesCollector(@NotNull PagePlacesEx pagePlaces) {
    myPagePlaces = pagePlaces;
  }

  @NotNull
  public Map<String, PlaceId> getPlaceIds() {
    Map<String, PlaceId> ids = getAllPlaceIds();
    if (ids.isEmpty()) return getAllPlaceIdsBefore2020_2();
    return ids;
  }

  @Nullable
  public PlaceId findByName(@Nullable String placeId) {
    if (placeId == null) return null;
    placeId = placeId.trim();

    Map<String, PlaceId> placeIds = getPlaceIds();
    return placeIds.get(placeId);
  }

  @NotNull
  private Map<String, PlaceId> getAllPlaceIds() {
    Map<String, PlaceId> res = new TreeMap<>(String::compareToIgnoreCase);
    try {
      Method registeredPlaceIdsMethod = PagePlacesEx.class.getMethod("getRegisteredPlaceIds");
      Object list = registeredPlaceIdsMethod.invoke(myPagePlaces);
      if (list instanceof List) {
        for (Object e: (List)list) {
          if (e instanceof PlaceId) {
            PlaceId id = (PlaceId) e;
            if (!isCustomTab(id)) {
              res.put(id.getId(), id);
            }
          } else {
            LOG.debug("Unexpected object type returned by the PagePlacesEx.getRegisteredPlaceIds() method: " + e.getClass().getName());
          }
        }
      }
    } catch (NoSuchMethodException e) {
      // this version of TeamCity does not allow to register a place id
    } catch (Exception e) {
      LOG.debug("Could not obtain a list of registered PlaceIds", e);
    }

    return res;
  }

  private boolean isCustomTab(@NotNull PlaceId id) {
    return id.getId().endsWith("@TAB");
  }

  @NotNull
  private Map<String, PlaceId> getAllPlaceIdsBefore2020_2() {
    Map<String, PlaceId> result = new TreeMap<>(String::compareToIgnoreCase);
    final Field[] fields = PlaceId.class.getDeclaredFields();

    for (Field field : fields) {
      final int modifiers = field.getModifiers();
      if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && Modifier.isPublic(modifiers) && PlaceId.class.equals(field.getType())) {
        field.setAccessible(true);
        try {
          PlaceId id = (PlaceId) field.get(null);
          if (isCustomTab(id)) continue;
          result.put(id.getId(), id);
          result.put(field.getName(), id); // left for compatibility
        } catch (IllegalAccessException e) {
          LOG.warn("Failed to read field of " + PlaceId.class);
        }
      }
    }
    return result;
  }
}

from abc import ABC, abstractmethod

from fastapi import Header


class ValidatorServiceInterface(ABC):
    @abstractmethod
    def validate(self, authorization: str):
        pass